package com.restfulapi.context;

import com.restfulapi.model.ApiObject;
import io.restassured.response.Response;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Per-scenario state store.
 * Spring's {@code "cucumber-glue"} scope ensures a fresh instance per scenario.
 */
@Component
@Scope("cucumber-glue")
@Data
public class ScenarioContext {

    private Response lastResponse;
    private String createdObjectId;

    /** Item name being built up across Given steps. */
    private String pendingRequestName;

    /** Data fields accumulated across Given steps before the When step fires. */
    private Map<String, Object> pendingRequestData;

    /** Extracts and caches the ID from the last create response. */
    public void captureCreatedObjectId() {
        if (lastResponse == null) {
            return;
        }
        this.createdObjectId = lastResponse.as(ApiObject.class).getId();
    }
}
