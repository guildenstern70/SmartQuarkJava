# AGENTS.md

## Scope and current shape
- This repo is a single-module Quarkus app (`build.gradle.kts`) using Gradle Kotlin DSL, Quarkus `3.34.3`, Lombok, MapStruct, Hibernate ORM/Panache, PostgreSQL, and Qute/REST Qute.
- Java is pinned to `VERSION_25`; keep new code compatible with that target.
- The only pre-existing agent-facing guidance was `README.md`; there were no other `AGENT*.md`, Copilot, Cursor, or Claude rule files.
- There is currently a single `@Path` resource in `src/main/java`: `src/main/java/net/littlelite/controller/web/IndexController.java`, which serves the Qute template at `src/main/resources/templates/index.html`. There are still no JSON REST endpoints.

## Where to look first
- `src/main/java/net/littlelite/Main.java`: Quarkus entry point; it just logs and waits for shutdown.
- `src/main/java/net/littlelite/service/StartupService.java`: real app bootstrap on `StartupEvent` / `ShutdownEvent`.
- `src/main/java/net/littlelite/service/DBInitializer.java`: database seeding logic and SQL-script execution.
- `src/main/java/net/littlelite/controller/web/IndexController.java`: the only current HTTP entry point; it renders the landing page.
- `src/main/resources/application.yml`: app metadata, logging format, and datasource kind.
- `src/main/resources/import.sql`: canonical seed data for `person` and `phone`.
- `src/main/resources/templates/index.html`: Qute template rendered for `/`.
- `src/main/java/net/littlelite/model/*.java`: the domain model (`Person`, `Phone`) built on Panache active record.

## Runtime/data flow
- Startup flow is `Main` → `StartupService.onStart(...)` → `TitleLogger.logTitle(...)` → `DBInitializer.populateDB()`.
- `StartupService` injects `SmartQuarkConfig` (`src/main/java/net/littlelite/config/SmartQuarkConfig.java`) plus `DBInitializer`, and logs the effective HTTP port via `@ConfigProperty(name = "quarkus.http.port")`.
- `DBInitializer.populateDB()` uses `Person.count()` to decide whether the DB is empty; if count is `0`, it loads `import.sql` from the classpath and runs it through an injected `DataSource`.
- `DBInitializer` strips `--` comments/blank lines, batches statements split on `;\s*\n`, disables auto-commit, then commits manually. Preserve that behavior if you change seeding.
- `import.sql` inserts explicit IDs for both tables and resets PostgreSQL sequences with `setval(...)`; if you add seed rows, update IDs/sequence expectations consistently.
- Web flow for the landing page is `GET /` → `IndexController.get()` → injected Qute `Template index` → `src/main/resources/templates/index.html`.

## Persistence and mapping conventions
- Entities extend `PanacheEntity` (`Person`, `Phone`) and startup code still uses active record patterns (`Person.count()`), but `src/main/java/net/littlelite/dao` now also contains `PanacheRepositoryBase` DAOs (`PersonDAO`, `PhoneDAO`). Those DAOs are not referenced by the current startup or web flow.
- The relationship is bidirectional: `Person.phoneNumbers` is `@OneToMany(mappedBy = "person", cascade = ALL, fetch = LAZY)` and `Phone.person` is `@ManyToOne`.
- Entity fields are `protected` and entities use Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@EqualsAndHashCode(callSuper = true)`). Match that style if you add model classes.
- DTOs in `src/main/java/net/littlelite/dto` still use `protected` fields, but they now also use Lombok `@Data` (`PersonDTO`, `PhoneDTO`).
- `src/main/java/net/littlelite/mapper/SimpleMapper.java` is a MapStruct `@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)`, so it is injectable via CDI/Jakarta. The current test coverage for it lives in `src/test/java/net/littlelite/mapper/SimpleMapperTest.java`.

## Config and integration assumptions
- `application.yml` only sets `quarkus.datasource.db-kind: postgresql`; JDBC URL/credentials are not committed here, so assume they come from external config/dev services.
- `SmartQuarkConfig` maps the custom `smartquark.*` namespace (`name`, `version`) via SmallRye `@ConfigMapping`.
- Logging is intentionally customized in `application.yml` and `TitleLogger`; startup banners are part of the current developer experience.
- The current UI surface is server-rendered via Qute: `IndexController` injects `Template index`, and Quarkus resolves that to `src/main/resources/templates/index.html`.

## Developer workflows verified here
- Dev mode: `./gradlew quarkusDev` (from `README.md`; Quarkus Dev UI should be at `http://localhost:8080/q/dev/` in dev mode).
- Tests: `./gradlew --no-daemon test` now runs `src/test/java/net/littlelite/mapper/SimpleMapperTest.java` under `@QuarkusTest`. In the current repo state it requires either a configured datasource URL or Docker-backed Quarkus Dev Services; otherwise the test task fails during datasource startup.
- Build/package: `./gradlew --no-daemon build -x test` succeeds and produces the runnable app under `build/quarkus-app/` plus the regular jar under `build/libs/`. A plain `build` also inherits the current test-environment requirement above.
- Native/container packaging instructions in `README.md` and Dockerfiles under `src/main/docker/` are standard Quarkus-generated scaffolding.

## Practical agent guidance
- Treat `build/` as generated output; change sources under `src/` and Gradle files, not packaged artifacts.
- If you add endpoints, keep them under `net.littlelite` (the current web entry point is `net.littlelite.controller.web.IndexController`) and wire them to the existing Panache/domain model rather than inventing a parallel persistence style.
- If you add seed logic, prefer extending `import.sql` and keep `DBInitializer`'s empty-DB gate intact so startup stays idempotent.
- If you change DTO or mapper structure, update `SimpleMapper` and `SimpleMapperTest` together so CDI-injected MapStruct mappings stay aligned.
- All Java files should be compliant with Allman style
- Unit Tests allow using of the databases, provided that tests are idempotent: they should not rely on a specific database state, and they should clean up any data they create. This allows tests to be run in any order and multiple times without side effects.

