package com.restfulapi.runner;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.SpringFactory;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Entry point for the Cucumber test suite.
 *
 * <p>Maven Failsafe picks this up via the include pattern
 * {@code *Runner.java} in the {@code runner} package during the
 * {@code integration-test} phase.
 *
 * <p>Override the tag filter at runtime:
 * <pre>
 *   mvn clean verify -Dcucumber.filter.tags="@smoke"
 * </pre>
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "com.restfulapi.config",
                "com.restfulapi.context",
                "com.restfulapi.hooks",
                "com.restfulapi.stepdefs"
        },
        tags = "not @wip",
        plugin = {
                "pretty",
                "json:target/cucumber-reports/cucumber.json"
        },
        objectFactory = SpringFactory.class
)
public class CucumberTestRunner {
    // intentionally empty
}
