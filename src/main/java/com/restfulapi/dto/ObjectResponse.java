package com.restfulapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Response DTO returned by all object endpoints.
 * - Contains id, name, data map, and optional createdAt/updatedAt timestamps
 * - Null fields are excluded from JSON output (@JsonInclude NON_NULL)
 * - Used for both single-object and list responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObjectResponse {

    private String id;
    private String name;
    private Map<String, Object> data;
    private String createdAt;
    private String updatedAt;
}
