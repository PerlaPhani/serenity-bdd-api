package com.restfulapi.helper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.Map;

public class RestAssuredHelper {

    private static final String DEFAULT_BASE_URL = "https://api.restful-api.dev";

    private final String baseUrl;

    public RestAssuredHelper() {
        this(DEFAULT_BASE_URL);
    }

    public RestAssuredHelper(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public static Response getAllObjects() {
        return new RestAssuredHelper().getAll();
    }

    public static Response getObjectById(String id) {
        return new RestAssuredHelper().getById(id);
    }

    public static Response createObject(Map<String, Object> body) {
        return new RestAssuredHelper().create(body);
    }

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
