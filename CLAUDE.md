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
| Validation       | Jakarta Bean Validation (spring-boot-starter-validation) | 3.0 |
| Boilerplate      | Lombok                        | 1.18.38  |
| Test Runner      | JUnit 5 Platform Suite (`@Suite` + `cucumber-junit-platform-engine`) |  |

## Architecture

### Layered Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     Controller                          │
│  ObjectController (@Valid, thin delegation)              │
├─────────────────────────────────────────────────────────┤
│                      Service                            │
│  ObjectService (business logic, throws exceptions)      │
├─────────────────────────────────────────────────────────┤
│                     Repository                          │
│  ObjectRepository (interface)                           │
│  └── InMemoryObjectRepository (ConcurrentHashMap, seed) │
├─────────────────────────────────────────────────────────┤
│               Cross-cutting Concerns                    │
│  GlobalExceptionHandler ── ObjectNotFoundException      │
│  ObjectMapper (Entity ↔ DTO)                            │
└─────────────────────────────────────────────────────────┘
```

### Package Structure

```
src/main/java/com/restfulapi/
├── RestfulApiApplication.java              # Spring Boot entry point
├── config/ApiConfig.java                   # Externalized API configuration
├── constants/Endpoints.java                # URI path constants
├── controller/ObjectController.java        # REST controller — thin, @Valid, delegates to service
├── dto/
│   ├── CreateObjectRequest.java            # Request DTO (@NotNull on name)
│   └── ObjectResponse.java                 # Response DTO (@JsonInclude NON_NULL)
├── entity/ObjectEntity.java                # Persistence model (no Jackson annotations)
├── exception/
│   ├── GlobalExceptionHandler.java         # @RestControllerAdvice (404 + validation errors)
│   └── ObjectNotFoundException.java        # Domain exception → 404
├── helper/ApiHelper.java                   # Single HTTP client (SerenityRest)
├── mapper/ObjectMapper.java                # Static Entity ↔ DTO conversions
├── repository/
│   ├── ObjectRepository.java               # Repository interface
│   └── InMemoryObjectRepository.java       # @Repository — ConcurrentHashMap + seed data
└── service/ObjectService.java              # Business logic — uses repository + mapper

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
│   ├── edge_cases.feature             # 5 scenarios (@edge)
│   └── smoke_test.feature             # 4 scenarios (@smoke)
├── application.properties              # Base test config (shared settings)
├── application-local.properties        # Local profile (localhost:8089)
├── application-dev.properties          # Dev profile (api.restful-api.dev)
├── junit-platform.properties           # Cucumber JUnit Platform Engine config
├── cucumber.properties                 # Cucumber publish settings
└── logback-test.xml                    # Logging config
```

## Design Principles

1. **Layered Architecture** — Controller → Service → Repository with clear separation of concerns
2. **Single Responsibility** — `ApiHelper` is the only class that calls SerenityRest/REST-Assured
3. **DTO/Entity Separation** — DTOs carry Jackson annotations for API contract; entities are plain persistence models
4. **Centralized Error Handling** — `GlobalExceptionHandler` (`@RestControllerAdvice`) owns all error responses; controller has no inline error logic
5. **Repository Pattern** — `ObjectRepository` interface abstracts storage; `InMemoryObjectRepository` is the current implementation (swappable for JPA/Mongo)
6. **Service Throws Exceptions** — Service methods throw `ObjectNotFoundException` instead of returning `Optional`, keeping the controller thin
7. **Bean Validation** — `@Valid` on controller request bodies + `@NotNull` on DTO fields; validation errors handled by `GlobalExceptionHandler`
8. **Dependency Injection** — All beans wired via Spring constructor injection, no manual instantiation
9. **Given/When/Then Separation** — Given steps build requests, When steps invoke APIs, Then steps assert responses
10. **Scenario Isolation** — `ScenarioContext` uses `cucumber-glue` scope (fresh per scenario) + `@After` hook cleans up created objects
11. **Builder Pattern Steps** — Incremental request construction across Given steps (name, CPU model, price)
12. **Configuration-Driven** — All connection properties externalized via Spring profiles + `application-{profile}.properties`
13. **Serenity Reporting** — Every `ApiHelper` method annotated with `@Step` for rich HTML reports
14. **Environment Portability** — Same test suite runs against local and dev via `-Dspring.profiles.active`

## Multi-Environment Support

The framework uses **Spring profiles** to switch between environments. Each profile has its own `application-{profile}.properties` that overrides `api.base-url` and `server.port`.

| Profile  | Base URL                          | Server Port | Description                        |
|----------|-----------------------------------|-------------|------------------------------------|
| `local`  | `http://localhost:8089`           | 8089        | Embedded Spring Boot API (default) |
| `dev`    | `https://api.restful-api.dev`     | 0 (random)  | Public restful-api.dev service     |

### How it works

- **Local** (default): The embedded Spring Boot API starts on port 8089 with 13 seeded objects. Tests run fully self-contained — no external dependencies.
- **Dev**: Tests point to the remote API. The embedded server still starts (on a random port) but is unused. Only `api.base-url` changes.

> **Rate limit note:** The public restful-api.dev API has a daily request limit of 50 requests on the free tier. If you hit a 405 response with a rate limit error, either wait for the daily reset, sign up at https://restful-api.dev/sign-in for higher limits, or run tests against the local environment instead.

### Running against each environment

```bash
# Local (default) — all 17 scenarios
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

## Test Scenarios (17 total across 6 feature files)

| Feature File            | Scenarios | Tag       | Coverage                                    |
|-------------------------|-----------|-----------|---------------------------------------------|
| `create_object.feature` | 2         | `@create` | Full details + minimal (name only)          |
| `get_object.feature`    | 2         | `@get`    | Retrieve created item + non-existent (404)  |
| `list_objects.feature`  | 2         | `@list`   | List all + verify created item in list      |
| `delete_object.feature` | 2         | `@delete` | Delete created item + non-existent (404)    |
| `edge_cases.feature`    | 5         | `@edge`   | Empty name, special chars, consistency, update persistence, delete confirmation |
| `smoke_test.feature`    | 4         | `@smoke`  | Health check, seeded object retrieval (x2), non-existent 404 |

### Tag Strategy

| Tag        | Purpose                                               |
|------------|-------------------------------------------------------|
| `@create`  | Object creation scenarios                             |
| `@get`     | Single object retrieval scenarios                     |
| `@list`    | List/browse scenarios                                 |
| `@delete`  | Object deletion scenarios                             |
| `@edge`    | Error handling and edge case scenarios                |
| `@smoke`   | Smoke tests for verifying basic API availability      |
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

# Smoke tests on dev
mvn clean verify -Dspring.profiles.active=dev \
  -Dcucumber.filter.tags="@smoke"

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

- Cleanup hooks auto-delete objects created during scenarios
- Profile-specific properties only override `api.base-url` and `server.port`
- `junit-platform.properties` resolves ObjectFactory SPI conflict between `serenity-cucumber` and `cucumber-spring`
