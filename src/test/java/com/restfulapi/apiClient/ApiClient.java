package com.restfulapi.apiClient;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;

public class ApiClient {

    private final String baseUri;
    private final String apiKey;

    public ApiClient(String baseUri, String apiKey) {
        this.baseUri = baseUri;
        this.apiKey = apiKey;
    }

    public Response get(String endpoint) {
        return SerenityRest
                .given()
                .header("x-api-key", apiKey)
                .baseUri(baseUri)
                .when()
                .get(endpoint)
                .then()
                .extract()
                .response();
    }

    public Response post(String endpoint, String jsonBody) {
        return SerenityRest
                .given()
                .header("x-api-key", apiKey)
                .header("Content-Type", ContentType.JSON)
                .body(jsonBody)
                .baseUri(baseUri)
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();
    }
}