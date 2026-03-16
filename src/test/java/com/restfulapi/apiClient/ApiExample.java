package com.restfulapi.apiClient;

import net.serenitybdd.rest.SerenityRest;
import io.restassured.response.Response;

public class ApiExample {

    public static void main(String[] args) {
//        String baseUrl = "https://api.restful-api.dev";
        String baseUrl = "https://jsonplaceholder.typicode.com";


        Response getResponse =
                SerenityRest
                        .given()
                        .header("x-api-key", "5e892f76-ed3b-4bd8-a292-a88422f1e225")
                        .baseUri(baseUrl)
                        .when()
//                        .get("/objects/7")
                        .get("/posts/1")
                        .then()
                        .extract()
                        .response();

        System.out.println("GET STATUS: " + getResponse.statusCode());
        System.out.println("GET BODY: " + getResponse.getBody().asString());
    }
}