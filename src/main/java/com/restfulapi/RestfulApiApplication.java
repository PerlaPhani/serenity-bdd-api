package com.restfulapi;

import com.restfulapi.config.ApiConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Spring Boot entry point for the Restful API application.
 * - Bootstraps the application context and starts the embedded server
 * - Enables externalized configuration via ApiConfig (@ConfigurationProperties)
 */
@SpringBootApplication
@EnableConfigurationProperties(ApiConfig.class)
public class RestfulApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestfulApiApplication.class, args);
    }
}
