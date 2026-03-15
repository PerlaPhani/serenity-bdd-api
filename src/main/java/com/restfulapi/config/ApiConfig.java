package com.restfulapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api")
@Data
public class ApiConfig {

    private String baseUrl;
    private int connectionTimeout;
    private int readTimeout;
    private boolean loggingEnabled;
}
