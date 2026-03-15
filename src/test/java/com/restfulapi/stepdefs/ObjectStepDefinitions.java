package com.restfulapi.stepdefs;

import com.restfulapi.context.ScenarioContext;
import com.restfulapi.helper.ApiHelper;
import com.restfulapi.model.ApiObject;
import com.restfulapi.model.CreateObjectRequest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * All Cucumber step implementations for the /objects resource.
 *
 * <p>Design rules:
 * <ul>
 *   <li>Beans are injected via {@code @Autowired} — never instantiated manually.</li>
 *   <li>Given steps may assert (pre-condition verification).</li>
 *   <li>When steps only invoke the API — no assertions.</li>
 *   <li>Then steps only assert — no API calls.</li>
 *   <li>Common patterns extracted to private helpers to eliminate duplication.</li>
 * </ul>
 */
public class ObjectStepDefinitions {

    @Autowired
    private ApiHelper apiHelper;

    @Autowired
    private ScenarioContext context;

    // ═══════════════════════════════════════════════════════════════════════
    // Private helpers
    // ═══════════════════════════════════════════════════════════════════════

    /** Returns the last captured response; fails fast if none exists yet. */
    private Response response() {
        assertThat(context.getLastResponse())
                .as("No API response has been captured yet — ensure a When step executed first")
                .isNotNull();
        return context.getLastResponse();
    }

    /** Returns the created-object ID; fails fast with a descriptive message if absent. */
    private String requireCreatedObjectId() {
        String id = context.getCreatedObjectId();
        assertThat(id)
                .as("No created-object ID is available — ensure an object-creation step ran first")
                .isNotBlank();
        return id;
    }

    /** Shared delete logic used by both single-delete and double-delete steps. */
    private Response executeDeleteOnCreatedObject() {
        return apiHelper.deleteObject(requireCreatedObjectId());
    }

    /**
     * Parses a raw IDs string like {@code "3","5","10"} into a trimmed List.
     * Handles both quoted and un-quoted variants.
     */
    private List<String> parseIds(String rawIds) {
        return Arrays.stream(rawIds.split(","))
                .map(s -> s.replace("\"", "").trim())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Converts a DataTable map into a {@link CreateObjectRequest}.
     * The "name" key maps to the top-level name field; all other keys go
     * into the {@code data} map.  Numeric-looking values are coerced to
     * {@code Long} or {@code Double} to match the API's expected types.
     */
    private CreateObjectRequest buildRequest(Map<String, String> table) {
        Map<String, Object> dataMap = new HashMap<>();
        String name = null;

        for (Map.Entry<String, String> entry : table.entrySet()) {
            if ("name".equalsIgnoreCase(entry.getKey())) {
                name = entry.getValue();
            } else {
                dataMap.put(entry.getKey(), coerce(entry.getValue()));
            }
        }

        return CreateObjectRequest.builder()
                .name(name)
                .data(dataMap.isEmpty() ? null : dataMap)
                .build();
    }

    private Object coerce(String value) {
        try { return Long.parseLong(value); }   catch (NumberFormatException ignored) { /* not an integer */ }
        try { return Double.parseDouble(value); } catch (NumberFormatException ignored) { /* not a decimal */ }
        return value;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GET /objects
    // ═══════════════════════════════════════════════════════════════════════

    @When("I retrieve all objects")
    public void iRetrieveAllObjects() {
        context.setLastResponse(apiHelper.getAllObjects());
    }

    @When("^I retrieve objects filtered by IDs (.+)$")
    public void iRetrieveObjectsFilteredByIds(String rawIds) {
        context.setLastResponse(apiHelper.getObjectsByIds(parseIds(rawIds)));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GET /objects/{id}
    // ═══════════════════════════════════════════════════════════════════════

    @When("I retrieve the object with ID {string}")
    public void iRetrieveTheObjectWithId(String id) {
        context.setLastResponse(apiHelper.getObjectById(id));
    }

    @When("I retrieve the created object by its ID")
    public void iRetrieveTheCreatedObjectByItsId() {
        context.setLastResponse(apiHelper.getObjectById(requireCreatedObjectId()));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // POST /objects — Given (with pre-condition assertion)
    // ═══════════════════════════════════════════════════════════════════════

    @Given("I have created an object with name {string}")
    public void iHaveCreatedAnObjectWithName(String name) {
        CreateObjectRequest request = CreateObjectRequest.builder()
                .name(name)
                .build();
        context.setLastResponse(apiHelper.createObject(request));
        assertThat(context.getLastResponse().getStatusCode())
                .as("Pre-condition: object creation must succeed (expected 200)")
                .isEqualTo(200);
        context.captureCreatedObjectId();
    }

    @Given("I have created an object with name {string} and price {string}")
    public void iHaveCreatedAnObjectWithNameAndPrice(String name, String price) {
        Map<String, Object> data = new HashMap<>();
        data.put("price", coerce(price));
        CreateObjectRequest request = CreateObjectRequest.builder()
                .name(name)
                .data(data)
                .build();
        context.setLastResponse(apiHelper.createObject(request));
        assertThat(context.getLastResponse().getStatusCode())
                .as("Pre-condition: object creation must succeed (expected 200)")
                .isEqualTo(200);
        context.captureCreatedObjectId();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // POST /objects — When (DataTable variant used in create + e2e scenarios)
    // ═══════════════════════════════════════════════════════════════════════

    @When("I create an object with the following details:")
    public void iCreateAnObjectWithTheFollowingDetails(Map<String, String> table) {
        context.setLastResponse(apiHelper.createObject(buildRequest(table)));
        if (context.getLastResponse().getStatusCode() == 200) {
            context.captureCreatedObjectId();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PUT /objects/{id}
    // ═══════════════════════════════════════════════════════════════════════

    @When("I fully update the created object with name {string} price {string} and color {string}")
    public void iFullyUpdateTheCreatedObject(String name, String price, String color) {
        String id = requireCreatedObjectId();
        Map<String, Object> data = new HashMap<>();
        data.put("price", coerce(price));
        data.put("color", color);
        CreateObjectRequest request = CreateObjectRequest.builder()
                .name(name)
                .data(data)
                .build();
        context.setLastResponse(apiHelper.updateObject(id, request));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PATCH /objects/{id}
    // ═══════════════════════════════════════════════════════════════════════

    @When("I partially update the created object name to {string}")
    public void iPartiallyUpdateTheCreatedObjectNameTo(String name) {
        String id = requireCreatedObjectId();
        CreateObjectRequest request = CreateObjectRequest.builder()
                .name(name)
                .build();
        context.setLastResponse(apiHelper.partiallyUpdateObject(id, request));
    }

    @When("I partially update the created object color to {string}")
    public void iPartiallyUpdateTheCreatedObjectColorTo(String color) {
        String id = requireCreatedObjectId();
        Map<String, Object> data = new HashMap<>();
        data.put("color", color);
        CreateObjectRequest request = CreateObjectRequest.builder()
                .data(data)
                .build();
        context.setLastResponse(apiHelper.partiallyUpdateObject(id, request));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DELETE /objects/{id}
    // ═══════════════════════════════════════════════════════════════════════

    @When("I delete the created object")
    public void iDeleteTheCreatedObject() {
        context.setLastResponse(executeDeleteOnCreatedObject());
    }

    @When("I delete the created object again")
    public void iDeleteTheCreatedObjectAgain() {
        context.setLastResponse(executeDeleteOnCreatedObject());
    }

    @When("I delete the object with ID {string}")
    public void iDeleteTheObjectWithId(String id) {
        context.setLastResponse(apiHelper.deleteObject(id));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Then — status code
    // ═══════════════════════════════════════════════════════════════════════

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatus) {
        assertThat(response().getStatusCode())
                .as("Expected HTTP status %d but received %d. Body: %s",
                        expectedStatus, response().getStatusCode(), response().asString())
                .isEqualTo(expectedStatus);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Then — list assertions
    // ═══════════════════════════════════════════════════════════════════════

    @Then("the response should contain a non-empty list of objects")
    public void theResponseShouldContainANonEmptyListOfObjects() {
        List<ApiObject> objects = Arrays.asList(response().as(ApiObject[].class));
        assertThat(objects)
                .as("Response should contain a non-empty list of objects")
                .isNotEmpty();
    }

    @Then("the response should contain exactly {int} objects")
    public void theResponseShouldContainExactlyObjects(int expected) {
        List<ApiObject> objects = Arrays.asList(response().as(ApiObject[].class));
        assertThat(objects)
                .as("Expected exactly %d objects in the response", expected)
                .hasSize(expected);
    }

    @Then("^the response should only contain objects with IDs (.+)$")
    public void theResponseShouldOnlyContainObjectsWithIds(String rawIds) {
        List<String> expectedIds = parseIds(rawIds);
        List<ApiObject> objects = Arrays.asList(response().as(ApiObject[].class));
        List<String> actualIds = objects.stream()
                .map(ApiObject::getId)
                .collect(Collectors.toList());

        assertThat(actualIds)
                .as("Response should contain exactly the requested IDs %s but got %s",
                        expectedIds, actualIds)
                .containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Then — object field assertions (unified step for all scenarios)
    // ═══════════════════════════════════════════════════════════════════════

    @Then("the response object should have name {string}")
    public void theResponseObjectShouldHaveName(String expectedName) {
        ApiObject obj = response().as(ApiObject.class);
        assertThat(obj.getName())
                .as("Expected object name to be '%s' but was '%s'", expectedName, obj.getName())
                .isEqualTo(expectedName);
    }

    @Then("the response should contain a createdAt timestamp")
    public void theResponseShouldContainACreatedAtTimestamp() {
        ApiObject obj = response().as(ApiObject.class);
        assertThat(obj.getCreatedAt())
                .as("Response should contain a non-blank createdAt timestamp")
                .isNotBlank();
    }

    @Then("the response should contain an updatedAt timestamp")
    public void theResponseShouldContainAnUpdatedAtTimestamp() {
        ApiObject obj = response().as(ApiObject.class);
        assertThat(obj.getUpdatedAt())
                .as("Response should contain a non-blank updatedAt timestamp")
                .isNotBlank();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Then — delete confirmation
    // ═══════════════════════════════════════════════════════════════════════

    @Then("the delete response message should confirm deletion")
    public void theDeleteResponseMessageShouldConfirmDeletion() {
        String body = response().asString();
        assertThat(body)
                .as("Delete response body should contain a deletion-confirmation message. Actual: %s", body)
                .containsIgnoringCase("deleted");
    }
}
