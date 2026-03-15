# Serenity BDD — Restful-API.dev Test Suite

End-to-end API test automation framework targeting **[restful-api.dev](https://restful-api.dev)**,
built with SerenityBDD + Cucumber (BDD), RestAssured, Spring Boot, and JUnit 4.

---

## Tech Stack

| Technology          | Version  | Role                                   |
|---------------------|----------|----------------------------------------|
| Java                | 17       | Language                               |
| SerenityBDD         | 4.1.20   | Test orchestration & HTML reporting    |
| Cucumber 7 (Gherkin)| 7.15.0   | BDD scenario definitions               |
| REST-Assured        | 5.3.2    | HTTP client                            |
| JUnit 4             | 4.13.2   | Test runner (CucumberWithSerenity)     |
| Spring Boot         | 3.2.1    | Dependency injection & configuration   |
| Lombok              | managed  | Boilerplate elimination                |
| AssertJ             | managed  | Fluent assertions                      |
| Maven               | 3.8+     | Build & dependency management          |

---

## Project Structure

```
serenity-restful-api/
├── pom.xml
├── .gitignore
│
├── src/main/java/com/restfulapi/
│   ├── RestfulApiApplication.java          # Spring Boot entry point
│   ├── config/
│   │   └── ApiConfig.java                  # @ConfigurationProperties (baseUrl, timeouts, logging)
│   ├── constants/
│   │   └── Endpoints.java                  # API path constants
│   ├── helper/
│   │   └── ApiHelper.java                  # The ONLY class that calls RestAssured / SerenityRest
│   └── model/
│       ├── ApiObject.java                  # Response model (id, name, data, createdAt, updatedAt)
│       └── CreateObjectRequest.java        # Request payload (name, data)
│
├── src/main/resources/
│   └── application.properties             # Base API config
│
├── src/test/java/com/restfulapi/
│   ├── config/
│   │   └── CucumberSpringConfiguration.java  # @CucumberContextConfiguration + @SpringBootTest
│   ├── context/
│   │   └── ScenarioContext.java              # Per-scenario state (cucumber-glue scope)
│   ├── runner/
│   │   └── CucumberTestRunner.java           # @RunWith(CucumberWithSerenity.class)
│   └── stepdefs/
│       └── ObjectStepDefinitions.java        # All Given/When/Then implementations
│
└── src/test/resources/
    ├── features/
    │   └── objects.feature                # All 26 Gherkin scenarios
    ├── application.properties             # Test config overrides
    ├── serenity.conf                      # Serenity report configuration
    └── logback-test.xml                   # Test logging configuration
```

---

## Architecture

```
CucumberTestRunner
       │  @RunWith(CucumberWithSerenity)
       ▼
ObjectStepDefinitions          ← @Autowired beans
       │
       ├──▶ ApiHelper          ← @Step-annotated, calls SerenityRest
       │         │
       │         └──▶ restful-api.dev  (HTTPS)
       │
       └──▶ ScenarioContext    ← @Scope("cucumber-glue"), fresh per scenario
                 │
                 └── lastResponse, createdObjectId, lastCreatedObject
```

**Key design decisions:**
- `ApiHelper` is the **only** class that calls RestAssured/SerenityRest — all HTTP traffic flows through it.
- Every `ApiHelper` method has `@Step` for full Serenity report visibility.
- `ScenarioContext` uses Spring's `"cucumber-glue"` scope — no manual `@Before`/`@After` cleanup needed.
- `Given` steps assert pre-conditions; `When` steps only invoke the API; `Then` steps only assert.

---

## Run Commands

```bash
# All tests + Serenity HTML report
mvn clean verify

# Smoke tests (critical path)
mvn clean verify -Dcucumber.filter.tags="@smoke"

# Full lifecycle end-to-end test
mvn clean verify -Dcucumber.filter.tags="@e2e"

# Negative / error-path tests
mvn clean verify -Dcucumber.filter.tags="@negative"

# All GET and POST scenarios
mvn clean verify -Dcucumber.filter.tags="@get or @post"

# All PUT + PATCH (update) scenarios
mvn clean verify -Dcucumber.filter.tags="@put or @patch"

# Persistence checks (POST then GET)
mvn clean verify -Dcucumber.filter.tags="@persistence"
```

**Report location:** `target/site/serenity/index.html`

---

## Tag Reference

| Tag              | Meaning                                     |
|------------------|---------------------------------------------|
| `@api @objects`  | Feature-level — all scenarios               |
| `@smoke`         | Critical-path scenarios                     |
| `@get`           | GET /objects scenarios                      |
| `@post`          | POST /objects scenarios                     |
| `@put`           | PUT /objects/{id} scenarios                 |
| `@patch`         | PATCH /objects/{id} scenarios               |
| `@delete`        | DELETE /objects/{id} scenarios              |
| `@list`          | Collection GET operations                   |
| `@single`        | Single-resource GET operations              |
| `@create`        | Object creation scenarios                   |
| `@update`        | Full-update scenarios                       |
| `@partial-update`| Partial-update scenarios                    |
| `@persistence`   | POST-then-GET data persistence checks       |
| `@negative`      | Error / sad-path scenarios (4xx responses)  |
| `@e2e @lifecycle`| Full Create→Read→PUT→PATCH→Delete flow     |
| `@wip`           | Excluded from default run (`not @wip`)      |

---

## Scenario Coverage

| Area               | Scenarios |
|--------------------|-----------|
| GET /objects (list)| 3         |
| GET /objects/{id}  | 5         |
| POST /objects      | 3         |
| Persistence checks | 2         |
| PUT /objects/{id}  | 2         |
| PATCH /objects/{id}| 3         |
| DELETE /objects/{id}| 4        |
| End-to-End         | 1         |
| **Total**          | **23**    |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- Internet access to `api.restful-api.dev`

```bash
mvn --version   # verify Maven
java -version   # verify Java 17+
```
