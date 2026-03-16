package com.restfulapi.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.FEATURES_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Entry point for the Cucumber test suite (JUnit 5 Platform Suite).
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
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.restfulapi.config,com.restfulapi.context,com.restfulapi.hooks,com.restfulapi.stepdefs")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @wip")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty,json:target/cucumber-reports/cucumber.json")
public class CucumberTestRunner {
    // intentionally empty — configuration is provided via annotations
}
