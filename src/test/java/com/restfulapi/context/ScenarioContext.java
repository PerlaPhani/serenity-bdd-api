package com.restfulapi.context;

import com.restfulapi.model.ApiObject;
import io.restassured.response.Response;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Per-scenario state store.
 *
 * <p>Spring's {@code "cucumber-glue"} scope ensures a fresh instance is
 * created for every Cucumber scenario — no manual cleanup required.
 */
@Component
@Scope("cucumber-glue")
@Data
public class ScenarioContext {

    private Response lastResponse;
    private String createdObjectId;
    private ApiObject lastCreatedObject;

    /** Holds the item name while building a request via Given steps. */
    private String pendingRequestName;

    /** Holds data fields accumulated across multiple Given steps before the When step fires. */
    private Map<String, Object> pendingRequestData;

    /**
     * Extracts the {@code id} from {@link #lastResponse} and caches it as
     * {@link #createdObjectId}.  Also stores the full deserialized object.
     *
     * <p>Call this immediately after any step that creates an object.
     */
    public void captureCreatedObjectId() {
        if (lastResponse == null) {
            return;
        }
        ApiObject obj = lastResponse.as(ApiObject.class);
        this.createdObjectId = obj.getId();
        this.lastCreatedObject = obj;
    }
}
