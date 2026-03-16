# SerenityBDD REST API Test Automation Framework

## Project Overview

A BDD test automation framework for CRUD operations against a REST API that mirrors [restful-api.dev](https://restful-api.dev/). The project includes both a **standalone Spring Boot API** (in-memory) and a **SerenityBDD/Cucumber test suite** that validates all endpoints. The same test suite runs against **local** and **dev** environments via Spring profiles.

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
| Test Runner      | JUnit 5 Platform Suite (`@Suite` + `cucumber-junit-platform-engine`) |  |

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
├── runner/CucumberTestRunner.java           # JUnit 5 Platform Suite entry point
└── stepdefs/ObjectStepDefinitions.java      # All step implementations

src/test/resources/
├── features/
│   ├── create_object.feature           # 2 scenarios (@create)
│   ├── get_object.feature              # 2 scenarios (@get)
│   ├── list_objects.feature            # 2 scenarios (@list)
│   ├── delete_object.feature           # 2 scenarios (@delete)
│   └── edge_cases.feature             # 5 scenarios (@edge)
├── application.properties              # Base test config (shared settings)
├── application-local.properties        # Local profile (localhost:8089)
├── application-dev.properties          # Dev profile (api.restful-api.dev)
├── junit-platform.properties           # Cucumber JUnit Platform Engine config
├── cucumber.properties                 # Cucumber publish settings
└── logback-test.xml                    # Logging config
```

## Design Principles

1. **Single Responsibility** — `ApiHelper` is the only class that calls SerenityRest/REST-Assured
2. **Dependency Injection** — All beans wired via Spring `@Autowired`, no manual instantiation
3. **Given/When/Then Separation** — Given steps build requests, When steps invoke APIs, Then steps assert responses
4. **Scenario Isolation** — `ScenarioContext` uses `cucumber-glue` scope (fresh per scenario) + `@After` hook cleans up created objects
5. **Builder Pattern Steps** — Incremental request construction across Given steps (name, CPU model, price)
6. **Configuration-Driven** — All connection properties externalized via Spring profiles + `application-{profile}.properties`
7. **Serenity Reporting** — Every `ApiHelper` method annotated with `@Step` for rich HTML reports
8. **Environment Portability** — Same test suite runs against local and dev via `-Dspring.profiles.active`

## Multi-Environment Support

The framework uses **Spring profiles** to switch between environments. Each profile has its own `application-{profile}.properties` that overrides `api.base-url` and `server.port`.

| Profile  | Base URL                          | Server Port | Description                        |
|----------|-----------------------------------|-------------|------------------------------------|
| `local`  | `http://localhost:8089`           | 8089        | Embedded Spring Boot API (default) |
| `dev`    | `https://api.restful-api.dev`     | 0 (random)  | Public restful-api.dev service     |

### How it works

- **Local** (default): The embedded Spring Boot API starts on port 8089 with 13 seeded objects. Tests run fully self-contained — no external dependencies.
- **Dev**: Tests point to the remote API. The embedded server still starts (on a random port) but is unused. Only `api.base-url` changes.

### Running against each environment

```bash
# Local (default) — all 13 scenarios
mvn clean verify

# Dev — exclude edge cases that depend on local seed data
mvn clean verify -Dspring.profiles.active=dev \
  -Dcucumber.filter.tags="not @wip and not @edge"
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

## Test Scenarios (13 total across 5 feature files)

| Feature File            | Scenarios | Tag       | Coverage                                    |
|-------------------------|-----------|-----------|---------------------------------------------|
| `create_object.feature` | 2         | `@create` | Full details + minimal (name only)          |
| `get_object.feature`    | 2         | `@get`    | Retrieve created item + non-existent (404)  |
| `list_objects.feature`  | 2         | `@list`   | List all + verify created item in list      |
| `delete_object.feature` | 2         | `@delete` | Delete created item + non-existent (404)    |
| `edge_cases.feature`    | 5         | `@edge`   | Empty name, special chars, consistency, update persistence, delete confirmation |

### Tag Strategy

| Tag        | Purpose                                               |
|------------|-------------------------------------------------------|
| `@create`  | Object creation scenarios                             |
| `@get`     | Single object retrieval scenarios                     |
| `@list`    | List/browse scenarios                                 |
| `@delete`  | Object deletion scenarios                             |
| `@edge`    | Error handling and edge case scenarios                |
| `@wip`     | Work in progress — excluded from all runs by default  |

## Running Tests

```bash
# All tests (local, default)
mvn clean verify

# By tag
mvn clean verify -Dcucumber.filter.tags="@create"
mvn clean verify -Dcucumber.filter.tags="@edge"
mvn clean verify -Dcucumber.filter.tags="@get or @delete"

# Dev environment
mvn clean verify -Dspring.profiles.active=dev

# Environment + tag combination
mvn clean verify -Dspring.profiles.active=dev \
  -Dcucumber.filter.tags="@create and not @wip"

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
| `src/test/resources/junit-platform.properties`     | Cucumber ObjectFactory (SpringFactory) |

Default profile is `local` (set in `pom.xml`). Override with `-Dspring.profiles.active=<profile>`.

## Conventions

- Feature files live in `src/test/resources/features/` (one per API capability)
- Step definitions live in `com.restfulapi.stepdefs`
- `@wip` tag excludes scenarios from default runs
- Cleanup hooks auto-delete objects created during scenarios
- Numeric string values are auto-coerced to Long/Double via `coerce()` helper
- Profile-specific properties only override `api.base-url` and `server.port`
- `junit-platform.properties` resolves ObjectFactory SPI conflict between `serenity-cucumber` and `cucumber-spring`
