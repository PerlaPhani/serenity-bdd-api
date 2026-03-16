package com.restfulapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized API configuration bound from application properties (prefix: "api").
 * - Holds baseUrl, loggingEnabled flag, and optional apiKey
 * - Consumed by ApiHelper to configure HTTP requests
 * - Profile-specific properties override baseUrl per environment (local, dev)
 */
@ConfigurationProperties(prefix = "api")
@Data
public class ApiConfig {
    private String baseUrl;
    private boolean loggingEnabled;
    private String apiKey;
}
