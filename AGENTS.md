# AGENTS.md

## Scope and current shape
- This repo is a single-module Quarkus app (`build.gradle.kts`) using Gradle Kotlin DSL, Quarkus `3.34.3`, Lombok, MapStruct, Hibernate ORM/Panache, PostgreSQL, Qute/REST Qute, SmallRye OpenAPI, SmallRye GraphQL, and SmallRye Health.
- Java is pinned to `VERSION_25`; keep new code compatible with that target.
- The only pre-existing agent-facing guidance was `README.md`; there were no other `AGENT*.md`, Copilot, Cursor, or Claude rule files.
- HTTP entry points are now split between the server-rendered landing page controller (`src/main/java/net/littlelite/controller/web/IndexController.java`), JSON REST controllers under `src/main/java/net/littlelite/controller/rest/` (`PersonController`, `PhoneController`), and the GraphQL API in `src/main/java/net/littlelite/graphql/PersonResource.java`.

## Where to look first
- `src/main/java/net/littlelite/Main.java`: Quarkus entry point; it just logs and waits for shutdown.
- `src/main/java/net/littlelite/service/StartupService.java`: real app bootstrap on `StartupEvent` / `ShutdownEvent`.
- `src/main/java/net/littlelite/service/DBInitializer.java`: database seeding logic and SQL-script execution.
- `src/main/java/net/littlelite/controller/web/IndexController.java`: renders the landing page and injects the Qute `index` template.
- `src/main/java/net/littlelite/controller/rest/PersonController.java` and `src/main/java/net/littlelite/controller/rest/PhoneController.java`: JSON CRUD endpoints delegating to the service layer.
- `src/main/java/net/littlelite/graphql/PersonResource.java`: GraphQL query surface for person data.
- `src/main/java/net/littlelite/service/PersonService.java` and `src/main/java/net/littlelite/service/PhoneService.java`: DTO-in/DTO-out CRUD logic used by the API layers.
- `src/main/resources/application.yml`: app metadata, logging format, and datasource kind.
- `src/main/resources/import.sql`: canonical seed data for `person` and `phone`.
- `src/main/resources/templates/index.qute.html`: Qute template rendered for `/`, with links to Swagger UI, GraphQL UI, Health UI, metrics, and the OpenAPI document.
- `src/main/java/net/littlelite/model/*.java`: the domain model (`Person`, `Phone`) built on Panache active record.

## Runtime/data flow
- Startup flow is `Main` → `StartupService.onStart(...)` → `TitleLogger.logTitle(...)` → `DBInitializer.populateDB()`.
- `StartupService` injects `SmartQuarkConfig` (`src/main/java/net/littlelite/config/SmartQuarkConfig.java`) plus `DBInitializer`, and logs the effective HTTP port via `@ConfigProperty(name = "quarkus.http.port")`.
- `DBInitializer.populateDB()` uses `Person.count()` to decide whether the DB is empty; if count is `0`, it loads `import.sql` from the classpath and runs it through an injected `DataSource`.
- `DBInitializer` strips `--` comments/blank lines, batches statements split on `;\s*\n`, disables auto-commit, then commits manually. Preserve that behavior if you change seeding.
- `import.sql` inserts explicit IDs for both tables and resets PostgreSQL sequences with `setval(...)`; if you add seed rows, update IDs/sequence expectations consistently.
- Web flow for the landing page is `GET /` → `IndexController.get()` → injected Qute `Template index` → `src/main/resources/templates/index.qute.html`.
- REST flows are `GET/POST/PUT/DELETE /api/persons` plus `POST /api/persons/by-id` → `PersonController` → `PersonService`, and the same shape under `/api/phones` → `PhoneController` → `PhoneService`.
- GraphQL flow is `Query allPersons` → `PersonResource.getAllPersons()` → `PersonService.findAll()`.
- Health and GraphQL UIs are extension-provided and enabled from `application.yml`; the landing page links to `/q/swagger-ui`, `/q/graphql-ui`, `/q/health-ui`, `/q/metrics`, and `/q/openapi?format=json`.

## Persistence and mapping conventions
- Entities extend `PanacheEntity` (`Person`, `Phone`) and startup code still uses active record patterns (`Person.count()`), but `src/main/java/net/littlelite/dao` now also contains `PanacheRepositoryBase` DAOs (`PersonDAO`, `PhoneDAO`). Those DAOs are not referenced by the current startup or web flow.
- The relationship is bidirectional: `Person.phoneNumbers` is `@OneToMany(mappedBy = "person", cascade = ALL, fetch = LAZY)` and `Phone.person` is `@ManyToOne`.
- Entity fields are `protected` and entities use Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@EqualsAndHashCode(callSuper = true)`). Match that style if you add model classes.
- DTOs in `src/main/java/net/littlelite/dto` still use `protected` fields, but they now also use Lombok `@Data` (`PersonDTO`, `PhoneDTO`).
- `src/main/java/net/littlelite/mapper/SimpleMapper.java` is a MapStruct `@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)`, so it is injectable via CDI/Jakarta. The current test coverage for it lives in `src/test/java/net/littlelite/mapper/SimpleMapperTest.java`.
- The API/service boundary is DTO-based: `PersonService` and `PhoneService` accept DTOs and return DTOs for CRUD methods, with `SimpleMapper` handling entity conversion.
- `SimpleMapper` maps `Person.phoneNumbers` ↔ `PersonDTO.phones`; if you rename either side, update both `@Mapping` annotations and the mapper tests.
- `PersonService.create(...)` / `update(...)` explicitly relink each `Phone` back to its owning `Person` before persistence, and `PhoneService.create(...)` / `update(...)` resolves `PhoneDTO.person.id` to a managed `Person`. Preserve those ownership rules if you change phone/person persistence.
- Service lookup/update/delete methods require DTO ids and throw `jakarta.ws.rs.NotFoundException` when the target row does not exist; the REST controllers currently pass those exceptions through.

## Config and integration assumptions
- `application.yml` sets `quarkus.datasource.db-kind: postgresql`; JDBC URL/credentials are not committed here, so assume they come from external config/dev services.
- `application.yml` also enables the SmallRye Health UI and GraphQL UI with `always-include: true`, and publishes OpenAPI metadata under `mp.openapi.extensions.smallrye.info`.
- `SmartQuarkConfig` maps the custom `smartquark.*` namespace (`name`, `version`) via SmallRye `@ConfigMapping`.
- Logging is intentionally customized in `application.yml` and `TitleLogger`; startup banners are part of the current developer experience.
- The current UI surface is server-rendered via Qute: `IndexController` injects `Template index`, and Quarkus resolves that to `src/main/resources/templates/index.qute.html`.
- There is no custom health check class in `src/main/java` at the moment; health endpoints come from the Quarkus SmallRye Health extension configuration.

## Developer workflows verified here
- Dev mode: `./gradlew quarkusDev` (from `README.md`; Quarkus Dev UI should be at `http://localhost:8080/q/dev/` in dev mode).
- Tests: `./gradlew --no-daemon test` now runs `src/test/java/net/littlelite/mapper/SimpleMapperTest.java`, `src/test/java/net/littlelite/service/PersonServiceTest.java`, and `src/test/java/net/littlelite/service/PhoneServiceTest.java` under `@QuarkusTest`. In the current repo state it requires either a configured datasource URL or Docker-backed Quarkus Dev Services; otherwise the test task fails during datasource startup.
- Build/package: `./gradlew --no-daemon build -x test` succeeds and produces the runnable app under `build/quarkus-app/` plus the regular jar under `build/libs/`. A plain `build` also inherits the current test-environment requirement above.
- Native/container packaging instructions in `README.md` and Dockerfiles under `src/main/docker/` are standard Quarkus-generated scaffolding.
- In dev mode, the implemented API surfaces are discoverable at `/q/swagger-ui`, `/q/graphql-ui`, `/q/health-ui`, and `/q/openapi`.

## Practical agent guidance
- Treat `build/` as generated output; change sources under `src/` and Gradle files, not packaged artifacts.
- If you add endpoints, keep them under `net.littlelite` and follow the existing split: server-rendered pages in `controller/web`, JSON APIs in `controller/rest`, and GraphQL entry points in `graphql`.
- Wire new API logic through `PersonService` / `PhoneService`-style DTO-based services rather than letting controllers talk directly to entities or DAOs.
- If you add seed logic, prefer extending `import.sql` and keep `DBInitializer`'s empty-DB gate intact so startup stays idempotent.
- If you change DTO, mapper, or service CRUD behavior, update `SimpleMapper`, `SimpleMapperTest`, and the focused service tests together so CDI-injected mappings and API expectations stay aligned.
- All Java files should be compliant with Allman style
- Unit Tests allow using of the databases, provided that tests are idempotent: they should not rely on a specific database state, and they should clean up any data they create. This allows tests to be run in any order and multiple times without side effects.

