# AGENTS.md

## Scope and current shape
- This repo is a single-module Quarkus app (`build.gradle.kts`) using Gradle Kotlin DSL, Quarkus `3.34.3`, Lombok, MapStruct, Hibernate ORM/Panache, and PostgreSQL.
- Java is pinned to `VERSION_25`; keep new code compatible with that target.
- The only pre-existing agent-facing guidance was `README.md`; there were no other `AGENT*.md`, Copilot, Cursor, or Claude rule files.
- Despite `quarkus-rest` and `quarkus-rest-jackson` dependencies, there are currently **no** `@Path` resources in `src/main/java`; this is a startup/persistence skeleton, not an exposed API yet.

## Where to look first
- `src/main/java/net/littlelite/Main.java`: Quarkus entry point; it just logs and waits for shutdown.
- `src/main/java/net/littlelite/service/StartupService.java`: real app bootstrap on `StartupEvent` / `ShutdownEvent`.
- `src/main/java/net/littlelite/service/DBInitializer.java`: database seeding logic and SQL-script execution.
- `src/main/resources/application.yml`: app metadata, logging format, and datasource kind.
- `src/main/resources/import.sql`: canonical seed data for `person` and `phone`.
- `src/main/java/net/littlelite/model/*.java`: the domain model (`Person`, `Phone`) built on Panache active record.

## Runtime/data flow
- Startup flow is `Main` → `StartupService.onStart(...)` → `TitleLogger.logTitle(...)` → `DBInitializer.populateDB()`.
- `StartupService` injects `SmartQuarkConfig` (`src/main/java/net/littlelite/config/SmartQuarkConfig.java`) plus `DBInitializer`, and logs the effective HTTP port via `@ConfigProperty(name = "quarkus.http.port")`.
- `DBInitializer.populateDB()` uses `Person.count()` to decide whether the DB is empty; if count is `0`, it loads `import.sql` from the classpath and runs it through an injected `DataSource`.
- `DBInitializer` strips `--` comments/blank lines, batches statements split on `;\s*\n`, disables auto-commit, then commits manually. Preserve that behavior if you change seeding.
- `import.sql` inserts explicit IDs for both tables and resets PostgreSQL sequences with `setval(...)`; if you add seed rows, update IDs/sequence expectations consistently.

## Persistence and mapping conventions
- Entities extend `PanacheEntity` (`Person`, `Phone`) and use active record patterns (`Person.count()`), not repository classes.
- The relationship is bidirectional: `Person.phoneNumbers` is `@OneToMany(mappedBy = "person", cascade = ALL, fetch = LAZY)` and `Phone.person` is `@ManyToOne`.
- Entity fields are `protected` and entities use Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@EqualsAndHashCode(callSuper = true)`). Match that style if you add model classes.
- DTOs exist in `src/main/java/net/littlelite/dto`, but they are plain classes with `protected` fields and no Lombok/accessors yet.
- `src/main/java/net/littlelite/mapper/SimpleMapper.java` is a bare MapStruct `@Mapper`; it is not referenced anywhere and has no CDI `componentModel`, so it is not injectable as-is.

## Config and integration assumptions
- `application.yml` only sets `quarkus.datasource.db-kind: postgresql`; JDBC URL/credentials are not committed here, so assume they come from external config/dev services.
- `SmartQuarkConfig` maps the custom `smartquark.*` namespace (`name`, `version`) via SmallRye `@ConfigMapping`.
- Logging is intentionally customized in `application.yml` and `TitleLogger`; startup banners are part of the current developer experience.

## Developer workflows verified here
- Dev mode: `./gradlew quarkusDev` (from `README.md`; Quarkus Dev UI should be at `http://localhost:8080/q/dev/` in dev mode).
- Tests: `./gradlew --no-daemon test` currently succeeds with `test NO-SOURCE` because there is no `src/test` tree yet.
- Build/package: `./gradlew --no-daemon build` succeeds and produces the runnable app under `build/quarkus-app/` plus the regular jar under `build/libs/`.
- Native/container packaging instructions in `README.md` and Dockerfiles under `src/main/docker/` are standard Quarkus-generated scaffolding.

## Practical agent guidance
- Treat `build/` as generated output; change sources under `src/` and Gradle files, not packaged artifacts.
- If you add endpoints, keep them under `net.littlelite` and wire them to the existing Panache/domain model rather than inventing a parallel persistence style.
- If you add seed logic, prefer extending `import.sql` and keep `DBInitializer`'s empty-DB gate intact so startup stays idempotent.
- All Java files should be compliant with Allman style

