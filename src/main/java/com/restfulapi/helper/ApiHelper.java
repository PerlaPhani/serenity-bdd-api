package com.restfulapi.helper;

import com.restfulapi.config.ApiConfig;
import com.restfulapi.constants.Endpoints;
import com.restfulapi.model.CreateObjectRequest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The single, authoritative HTTP client for the Restful-API.
 *
 * <p>Rules:
 * <ul>
 *   <li>Only class that calls SerenityRest / RestAssured.</li>
 *   <li>Every public method carries {@code @Step} for Serenity report visibility.</li>
 *   <li>Every method builds a fresh {@link RequestSpecification} via
 *       {@link #buildRequestSpec()}.</li>
 *   <li>Every method returns the raw {@link Response} — no assertions, no test
 *       state stored here.</li>
 * </ul>
 */
@Component
public class ApiHelper {

    @Autowired
    private ApiConfig apiConfig;

    // ── private builder ──────────────────────────────────────────────────────

    private RequestSpecification buildRequestSpec() {
        RequestSpecification spec = SerenityRest.given()
                .baseUri(apiConfig.getBaseUrl())
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");

        if (apiConfig.getApiKey() != null && !apiConfig.getApiKey().isBlank()) {
            spec = spec.header("x-api-key", apiConfig.getApiKey());
        }

        if (apiConfig.isLoggingEnabled()) {
            spec = spec.log().all();
        }
        return spec;
    }

    // ── GET ──────────────────────────────────────────────────────────────────

    @Step("GET /objects — retrieve all objects")
    public Response getAllObjects() {
        return buildRequestSpec()
                .get(Endpoints.OBJECTS);
    }

    @Step("GET /objects?id=... — retrieve objects by IDs: {0}")
    public Response getObjectsByIds(List<String> ids) {
        RequestSpecification spec = buildRequestSpec();
        for (String id : ids) {
            spec = spec.queryParam(Endpoints.QUERY_PARAM_ID, id);
        }
        return spec.get(Endpoints.OBJECTS);
    }

    @Step("GET /objects/{0} — retrieve object by ID")
    public Response getObjectById(String id) {
        return buildRequestSpec()
                .pathParam(Endpoints.PATH_PARAM_ID, id)
                .get(Endpoints.OBJECT_BY_ID);
    }

    // ── POST ─────────────────────────────────────────────────────────────────

    @Step("POST /objects — create object: {0}")
    public Response createObject(CreateObjectRequest request) {
        return buildRequestSpec()
                .body(request)
                .post(Endpoints.OBJECTS);
    }

    // ── PUT ──────────────────────────────────────────────────────────────────

    @Step("PUT /objects/{0} — full update")
    public Response updateObject(String id, CreateObjectRequest request) {
        return buildRequestSpec()
                .pathParam(Endpoints.PATH_PARAM_ID, id)
                .body(request)
                .put(Endpoints.OBJECT_BY_ID);
    }

    // ── PATCH ────────────────────────────────────────────────────────────────

    @Step("PATCH /objects/{0} — partial update")
    public Response partiallyUpdateObject(String id, CreateObjectRequest request) {
        return buildRequestSpec()
                .pathParam(Endpoints.PATH_PARAM_ID, id)
                .body(request)
                .patch(Endpoints.OBJECT_BY_ID);
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    @Step("DELETE /objects/{0}")
    public Response deleteObject(String id) {
        return buildRequestSpec()
                .pathParam(Endpoints.PATH_PARAM_ID, id)
                .delete(Endpoints.OBJECT_BY_ID);
    }
}
