package com.restfulapi.helper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

/**
 * Lightweight REST helper using plain RestAssured.
 * No Spring or Serenity dependencies — can be used standalone.
 *
 * <p>Usage:
 * <pre>
 *   // Default — hits https://api.restful-api.dev
 *   Response r = RestAssuredHelper.getAllObjects();
 *
 *   // Custom base URL
 *   RestAssuredHelper helper = new RestAssuredHelper("http://localhost:8089");
 *   Response r = helper.getAll();
 * </pre>
 */
public class RestAssuredHelper {

    private static final String DEFAULT_BASE_URL = "https://api.restful-api.dev";

    private final String baseUrl;

    public RestAssuredHelper() {
        this(DEFAULT_BASE_URL);
    }

    public RestAssuredHelper(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // ── Static convenience methods (use default base URL) ─────────────────

    /**
     * GET /objects — retrieve all objects.
     */
    public static Response getAllObjects() {
        return new RestAssuredHelper().getAll();
    }

    /**
     * GET /objects/{id} — retrieve a single object by ID.
     */
    public static Response getObjectById(String id) {
        return new RestAssuredHelper().getById(id);
    }

    /**
     * POST /objects — create a new object.
     */
    public static Response createObject(Map<String, Object> body) {
        return new RestAssuredHelper().create(body);
    }

    // ── Instance methods (use configured base URL) ────────────────────────

    public Response getAll() {
        return RestAssured.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .log().all()
                .when()
                .get("/objects")
                .then()
                .log().all()
                .extract().response();
    }

    public Response getById(String id) {
        return RestAssured.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .log().all()
                .when()
                .get("/objects/{id}", id)
                .then()
                .log().all()
                .extract().response();
    }

    public Response create(Map<String, Object> body) {
        return RestAssured.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .log().all()
                .when()
                .post("/objects")
                .then()
                .log().all()
                .extract().response();
    }
}
