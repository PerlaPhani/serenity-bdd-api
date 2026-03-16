package com.restfulapi.helper;

import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates RestAssuredHelper usage against the default public API.
 *
 * <p>Run with: {@code mvn test -DskipUnitTests=false -Dtest=RestAssuredHelperTest}
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RestAssuredHelperTest {

    @Test
    @Order(1)
    void getAllObjects_returnsListOfObjects() {
        Response response = RestAssuredHelper.getAllObjects();

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("$")).isNotEmpty();
        System.out.println("Total objects: " + response.jsonPath().getList("$").size());
    }

    @Test
    @Order(2)
    void getObjectById_returnsCorrectObject() {
        Response response = RestAssuredHelper.getObjectById("7");

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("name")).isEqualTo("Apple MacBook Pro 16");
        System.out.println("Object name: " + response.jsonPath().getString("name"));
    }

    @Test
    @Order(3)
    void createObject_returnsCreatedObject() {
        Map<String, Object> body = Map.of(
                "name", "Google Pixel 9",
                "data", Map.of(
                        "color", "Obsidian",
                        "capacity", "256 GB",
                        "price", 799.99
                )
        );

        Response response = RestAssuredHelper.createObject(body);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("name")).isEqualTo("Google Pixel 9");
        assertThat(response.jsonPath().getString("id")).isNotBlank();
        System.out.println("Created object ID: " + response.jsonPath().getString("id"));
    }
}
