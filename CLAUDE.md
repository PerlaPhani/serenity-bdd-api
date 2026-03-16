# SerenityBDD REST API Test Automation Framework

## Project Overview

A BDD test automation framework for CRUD operations against a REST API that mirrors [restful-api.dev](https://restful-api.dev/). The project includes both a **standalone Spring Boot API** (in-memory) and a **SerenityBDD/Cucumber test suite** that validates all endpoints. The same test suite runs against **local, dev, test, and prod** environments via Spring profiles.

## Tech Stack

| Layer            | Technology                    | Version  |
|------------------|-------------------------------|----------|
| Language         | Java                          | 17       |
| Build            | Maven                         | 3.x      |
| Framework        | Spring Boot                   | 3.2.1    |
| BDD              | Cucumber 7                    | 7.22.2   |
| Test Orchestration | SerenityBDD                 | 4.2.34   |
| HTTP Client      | REST-Assured (via SerenityRest) | 5.3.2  |
| Assertions       | AssertJ + REST-Assured JsonPath |        |
| Boilerplate      | Lombok                        | 1.18.38  |
| Test Runner      | JUnit 4 (CucumberWithSerenity) | 4.13.2  |

## Architecture

```
src/main/java/com/restfulapi/
├── RestfulApiApplication.java          # Spring Boot entry point
├── config/ApiConfig.java               # Externalized API configuration
├── constants/Endpoints.java            # URI path constants
├── controller/ObjectController.java    # REST controller (GET/POST/PUT/PATCH/DELETE)
├── helper/ApiHelper.java               # Single HTTP client (SerenityRest)
├── model/
│   ├── ApiObject.java                  # Response model
│   └── CreateObjectRequest.java        # Request payload model
└── service/ObjectService.java          # In-memory CRUD service with seed data

src/test/java/com/restfulapi/
├── config/CucumberSpringConfiguration.java  # Spring Boot test context bridge
├── context/ScenarioContext.java             # Per-scenario state (cucumber-glue scope)
├── hooks/ScenarioHooks.java                 # @Before/@After lifecycle hooks
├── runner/CucumberTestRunner.java           # Cucumber entry point
└── stepdefs/ObjectStepDefinitions.java      # All step implementations

src/test/resources/
├── features/objects.feature            # 30 BDD scenarios
├── application.properties              # Base test config (shared settings)
├── application-local.properties        # Local profile (localhost:8089)
├── application-dev.properties          # Dev profile (api.restful-api.dev)
├── application-test.properties         # Test profile (test-api.example.com)
├── application-prod.properties         # Prod profile (prod-api.example.com)
└── logback-test.xml                    # Logging config
```

## Design Principles

1. **Single Responsibility** — `ApiHelper` is the only class that calls SerenityRest/REST-Assured
2. **Dependency Injection** — All beans wired via Spring `@Autowired`, no manual instantiation
3. **Given/When/Then Separation** — Given steps assert pre-conditions, When steps invoke APIs only, Then steps assert only
4. **Scenario Isolation** — `ScenarioContext` uses `cucumber-glue` scope (fresh per scenario) + `@After` hook cleans up created objects
5. **JSON Path Assertions** — Reusable `the JSON path "<path>" should equal "<value>"` step for deep field validation
6. **Builder Pattern Steps** — Supports incremental request construction across Given steps (PDF example style)
7. **Configuration-Driven** — All connection properties externalized via Spring profiles + `application-{profile}.properties`
8. **Serenity Reporting** — Every `ApiHelper` method annotated with `@Step` for rich HTML reports
9. **Environment Portability** — Same test suite runs against local, dev, test, and prod via `-Dspring.profiles.active`

## Multi-Environment Support

The framework uses **Spring profiles** to switch between environments. Each profile has its own `application-{profile}.properties` that overrides `api.base-url` and `server.port`.

| Profile  | Base URL                          | Server Port | Description                        |
|----------|-----------------------------------|-------------|------------------------------------|
| `local`  | `http://localhost:8089`           | 8089        | Embedded Spring Boot API (default) |
| `dev`    | `https://api.restful-api.dev`     | 0 (random)  | Public restful-api.dev service     |
| `test`   | `https://test-api.example.com`    | 0 (random)  | Test environment (update URL)      |
| `prod`   | `https://prod-api.example.com`    | 0 (random)  | Production environment (update URL)|

### How it works

- **Local** (default): The embedded Spring Boot API starts on port 8089 with 13 seeded objects. Tests run fully self-contained — no external dependencies.
- **Dev/Test/Prod**: Tests point to the remote API. The embedded server still starts (on a random port) but is unused. Only `api.base-url` changes.
- The `@seed-data` tag marks scenarios that depend on pre-seeded objects (specific IDs like "7" = "Apple MacBook Pro 16"). These should be excluded on remote environments where seed data isn't guaranteed.

### Running against each environment

```bash
# Local (default) — all 30 scenarios
mvn clean verify

# Dev — exclude seed-data scenarios (remote API has rate limits)
mvn clean verify -Dspring.profiles.active=dev \
  -Dcucumber.filter.tags="not @wip and not @seed-data"

# Test environment
mvn clean verify -Dspring.profiles.active=test \
  -Dcucumber.filter.tags="not @wip and not @seed-data"

# Prod — smoke tests only
mvn clean verify -Dspring.profiles.active=prod \
  -Dcucumber.filter.tags="@smoke and not @seed-data"
```

### Adding a new environment

1. Create `src/test/resources/application-{name}.properties`:
   ```properties
   server.port=0
   api.base-url=https://your-api-host.com
   ```
2. Run with `-Dspring.profiles.active={name}`

## API Endpoints (Standalone Server)

| Method | Endpoint               | Description                          |
|--------|------------------------|--------------------------------------|
| GET    | `/objects`             | List all objects (supports `?id=` filtering) |
| GET    | `/objects/{id}`        | Retrieve single object               |
| POST   | `/objects`             | Create object (returns `createdAt`)  |
| PUT    | `/objects/{id}`        | Full replacement update (`updatedAt`) |
| PATCH  | `/objects/{id}`        | Partial merge update (`updatedAt`)   |
| DELETE | `/objects/{id}`        | Delete object (confirmation message) |

The API is seeded with 13 objects (IDs 1-13) matching restful-api.dev data on startup.

## Test Scenarios (30 total)

| Category                | Count | Tags                        |
|-------------------------|-------|-----------------------------|
| GET list                | 4     | `@get @list`                |
| GET single              | 5     | `@get @single`              |
| POST create             | 5     | `@post @create`             |
| POST->GET persistence   | 3     | `@persistence`              |
| PUT full update         | 2     | `@put @update`              |
| PATCH partial update    | 3     | `@patch @partial-update`    |
| DELETE                  | 5     | `@delete`                   |
| E2E lifecycle           | 1     | `@e2e @lifecycle`           |
| JSON Path validation    | 7     | `@jsonpath`                 |
| Negative/error paths    | 5     | `@negative`                 |
| Smoke (critical path)   | 7     | `@smoke`                    |
| Seed-data dependent     | 7     | `@seed-data`                |

### Tag Strategy

| Tag               | Purpose                                                |
|-------------------|--------------------------------------------------------|
| `@smoke`          | Critical path — run on every environment               |
| `@seed-data`      | Depends on pre-seeded objects — exclude on remote envs |
| `@negative`       | Error/edge case paths (404, invalid IDs)               |
| `@jsonpath`       | Scenarios using JSON Path assertions                   |
| `@e2e`            | Full CRUD lifecycle                                    |
| `@persistence`    | Create-then-retrieve validation                        |
| `@wip`            | Work in progress — excluded from all runs by default   |

## Running Tests

```bash
# All tests (local, default)
mvn clean verify

# By tag
mvn clean verify -Dcucumber.filter.tags="@smoke"
mvn clean verify -Dcucumber.filter.tags="@jsonpath"
mvn clean verify -Dcucumber.filter.tags="@negative"
mvn clean verify -Dcucumber.filter.tags="@e2e"
mvn clean verify -Dcucumber.filter.tags="@get or @post"

# By environment
mvn clean verify -Dspring.profiles.active=dev
mvn clean verify -Dspring.profiles.active=test
mvn clean verify -Dspring.profiles.active=prod

# Environment + tag combination
mvn clean verify -Dspring.profiles.active=dev \
  -Dcucumber.filter.tags="@smoke and not @seed-data"

# Run standalone API (port 8080)
mvn spring-boot:run
```

## Reports

After `mvn clean verify`, open:
```
target/site/serenity/index.html
```

## Configuration

| File                                         | Purpose                        |
|----------------------------------------------|--------------------------------|
| `src/main/resources/application.properties`  | Standalone server (port 8080)  |
| `src/test/resources/application.properties`  | Base test config (shared)      |
| `src/test/resources/application-local.properties`  | Local embedded server    |
| `src/test/resources/application-dev.properties`    | Dev (restful-api.dev)    |
| `src/test/resources/application-test.properties`   | Test environment         |
| `src/test/resources/application-prod.properties`   | Production environment   |

Default profile is `local` (set in `pom.xml`). Override with `-Dspring.profiles.active=<profile>`.

## Conventions

- Feature files live in `src/test/resources/features/`
- Step definitions live in `com.restfulapi.stepdefs`
- One feature file per API resource
- `@wip` tag excludes scenarios from default runs
- `@seed-data` tag marks scenarios that depend on pre-seeded objects (exclude for remote envs)
- Cleanup hooks auto-delete objects created during scenarios
- Numeric string values in DataTables are auto-coerced to Long/Double
- Profile-specific properties only override `api.base-url` and `server.port`
