package com.restfulapi.config;

import com.restfulapi.RestfulApiApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Bridges Cucumber and the Spring Boot test context.
 *
 * <p>{@code @CucumberContextConfiguration} tells Cucumber to bootstrap the
 * Spring application context once per test run and inject beans into glue
 * classes.  {@code @SpringBootTest} loads the full application context from
 * {@link RestfulApiApplication}.
 */
@CucumberContextConfiguration
@SpringBootTest(
        classes = RestfulApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class CucumberSpringConfiguration {
    // configuration is provided via annotations
}
