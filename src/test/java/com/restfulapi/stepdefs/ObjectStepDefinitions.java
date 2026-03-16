package com.restfulapi.stepdefs;

import com.restfulapi.context.ScenarioContext;
import com.restfulapi.helper.ApiHelper;
import com.restfulapi.model.ApiObject;
import com.restfulapi.model.CreateObjectRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectStepDefinitions {

    @Autowired
    private ApiHelper apiHelper;

    @Autowired
    private ScenarioContext context;

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Response response() {
        assertThat(context.getLastResponse())
                .as("No API response captured — ensure a When step ran first")
                .isNotNull();
        return context.getLastResponse();
    }

    private String requireCreatedObjectId() {
        String id = context.getCreatedObjectId();
        assertThat(id)
                .as("No created-object ID — ensure an object was created first")
                .isNotBlank();
        return id;
    }

    private Object coerce(String value) {
        try { return Long.parseLong(value); }   catch (NumberFormatException ignored) {}
        try { return Double.parseDouble(value); } catch (NumberFormatException ignored) {}
        return value;
    }

    // ── Given — builder-pattern request construction ─────────────────────────

    @Given("a {string} item is created")
    public void anItemIsCreated(String name) {
        context.setPendingRequestName(name);
        context.setPendingRequestData(new HashMap<>());
    }

    @Given("the CPU model is {string}")
    public void theCpuModelIs(String cpuModel) {
        context.getPendingRequestData().put("CPU model", cpuModel);
    }

    @Given("has a price of {string}")
    public void hasAPriceOf(String price) {
        context.getPendingRequestData().put("price", coerce(price));
    }

    // ── When — API calls ─────────────────────────────────────────────────────

    @When("the request to add the item is made")
    public void theRequestToAddTheItemIsMade() {
        Map<String, Object> data = context.getPendingRequestData();
        CreateObjectRequest request = CreateObjectRequest.builder()
                .name(context.getPendingRequestName())
                .data(data.isEmpty() ? null : data)
                .build();
        context.setLastResponse(apiHelper.createObject(request));
        if (context.getLastResponse().getStatusCode() == 200) {
            context.captureCreatedObjectId();
        }
    }

    @When("the item is retrieved by its ID")
    public void theItemIsRetrievedByItsId() {
        context.setLastResponse(apiHelper.getObjectById(requireCreatedObjectId()));
    }

    @When("the item is retrieved by ID {string}")
    public void theItemIsRetrievedById(String id) {
        context.setLastResponse(apiHelper.getObjectById(id));
    }

    @When("a request to list all objects is made")
    public void aRequestToListAllObjectsIsMade() {
        context.setLastResponse(apiHelper.getAllObjects());
    }

    @When("the item is deleted by its ID")
    public void theItemIsDeletedByItsId() {
        context.setLastResponse(apiHelper.deleteObject(requireCreatedObjectId()));
    }

    @When("the item is deleted by ID {string}")
    public void theItemIsDeletedById(String id) {
        context.setLastResponse(apiHelper.deleteObject(id));
    }

    @When("the item is updated with name {string} and price {string}")
    public void theItemIsUpdatedWithNameAndPrice(String name, String price) {
        Map<String, Object> data = new HashMap<>();
        data.put("price", coerce(price));
        CreateObjectRequest request = CreateObjectRequest.builder()
                .name(name)
                .data(data)
                .build();
        context.setLastResponse(apiHelper.updateObject(requireCreatedObjectId(), request));
    }

    // ── Then — assertions ────────────────────────────────────────────────────

    @Then("a {int} response code is returned")
    public void aResponseCodeIsReturned(int expectedStatus) {
        assertThat(response().getStatusCode())
                .as("Expected HTTP %d but got %d. Body: %s",
                        expectedStatus, response().getStatusCode(), response().asString())
                .isEqualTo(expectedStatus);
    }

    @Then("a {string} is created")
    public void anItemIsCreatedWithName(String expectedName) {
        assertThat(response().jsonPath().getString("name"))
                .as("Created object name")
                .isEqualTo(expectedName);
    }

    @Then("the response contains a non-null {string}")
    public void theResponseContainsANonNull(String field) {
        Object value = response().jsonPath().get(field);
        assertThat(value)
                .as("Field '%s' should not be null", field)
                .isNotNull();
    }

    @Then("the response {string} timestamp is present")
    public void theResponseTimestampIsPresent(String field) {
        assertThat(response().jsonPath().getString(field))
                .as("Timestamp '%s' should be present", field)
                .isNotBlank();
    }

    @Then("the response name is {string}")
    public void theResponseNameIs(String expectedName) {
        assertThat(response().jsonPath().getString("name"))
                .as("Response name")
                .isEqualTo(expectedName);
    }

    @Then("the response data contains {string} for {string}")
    public void theResponseDataContainsValueForKey(String expectedValue, String key) {
        Object actual = response().jsonPath().get("data.'" + key + "'");
        try {
            double expectedNum = Double.parseDouble(expectedValue);
            assertThat(Double.parseDouble(String.valueOf(actual)))
                    .as("data.'%s'", key)
                    .isEqualTo(expectedNum);
        } catch (NumberFormatException e) {
            assertThat(String.valueOf(actual))
                    .as("data.'%s'", key)
                    .isEqualTo(expectedValue);
        }
    }

    @Then("the response contains an error message")
    public void theResponseContainsAnErrorMessage() {
        Object error = response().jsonPath().get("error");
        assertThat(error)
                .as("Error field should be present")
                .isNotNull();
    }

    @Then("the response contains a list of objects")
    public void theResponseContainsAListOfObjects() {
        List<ApiObject> objects = Arrays.asList(response().as(ApiObject[].class));
        assertThat(objects).as("Object list should not be empty").isNotEmpty();
    }

    @Then("the list contains an item with name {string}")
    public void theListContainsAnItemWithName(String expectedName) {
        List<ApiObject> objects = Arrays.asList(response().as(ApiObject[].class));
        assertThat(objects)
                .as("List should contain an item named '%s'", expectedName)
                .anyMatch(obj -> expectedName.equals(obj.getName()));
    }

    @Then("the item no longer exists when retrieved")
    public void theItemNoLongerExistsWhenRetrieved() {
        Response getResponse = apiHelper.getObjectById(requireCreatedObjectId());
        assertThat(getResponse.getStatusCode())
                .as("Deleted item should return 404")
                .isEqualTo(404);
    }

    @Then("the response indicates a validation issue or accepts the request")
    public void theResponseIndicatesValidationOrAccepts() {
        int status = response().getStatusCode();
        assertThat(status)
                .as("Should be 200 (accepted) or 4xx (validation error)")
                .satisfiesAnyOf(
                        s -> assertThat(s).isEqualTo(200),
                        s -> assertThat(s).isBetween(400, 499)
                );
    }
}
