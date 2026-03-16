package com.restfulapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for creating or updating an object.
 * - name field is required (@NotNull) for POST and PUT
 * - data is an optional key-value map for object attributes
 * - Null fields are excluded from JSON serialization (@JsonInclude NON_NULL)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateObjectRequest {

    @NotNull
    private String name;
    private Map<String, Object> data;
}
