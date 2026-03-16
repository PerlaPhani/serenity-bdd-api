package com.restfulapi.hooks;

import com.restfulapi.context.ScenarioContext;
import com.restfulapi.helper.ApiHelper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * Cucumber lifecycle hooks for setup and cleanup.
 *
 * <p>{@code @Before} runs before each scenario to reset state.
 * {@code @After} runs after each scenario to clean up any objects
 * created during the test, ensuring test isolation.
 */
public class ScenarioHooks {

    private static final Logger LOG = LoggerFactory.getLogger(ScenarioHooks.class);

    @Autowired
    private ScenarioContext context;

    @Autowired
    private ApiHelper apiHelper;

    @Before
    public void setUp(Scenario scenario) {
        LOG.info("Starting scenario: {}", scenario.getName());
    }

    @After
    public void cleanUp(Scenario scenario) {
        String createdId = context.getCreatedObjectId();
        if (createdId != null && !createdId.isBlank()) {
            LOG.info("Cleaning up: deleting object with ID {} created during scenario '{}'",
                    createdId, scenario.getName());
            try {
                apiHelper.deleteObject(createdId);
            } catch (Exception e) {
                LOG.warn("Cleanup deletion failed for object ID {} (may already be deleted): {}",
                        createdId, e.getMessage());
            }
        }
        LOG.info("Finished scenario: {} — Status: {}", scenario.getName(), scenario.getStatus());
    }
}
