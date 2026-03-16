package com.restfulapi.apiClient;

import io.restassured.response.Response;

public class TestApiClient {
    public static void main(String[] args) {

        ApiClient client = new ApiClient(
                "https://jsonplaceholder.typicode.com",
                ""
        );

        Response getResponse = client.get("/posts/1");
        System.out.println("GET STATUS: " + getResponse.statusCode());
        System.out.println("GET BODY: " + getResponse.getBody().asString());

        String json = "{\"title\":\"foo\",\"body\":\"bar\",\"userId\":1}";
        Response postResponse = client.post("/posts", json);
        System.out.println("POST STATUS: " + postResponse.statusCode());
        System.out.println("POST BODY: " + postResponse.getBody().asString());
    }
}