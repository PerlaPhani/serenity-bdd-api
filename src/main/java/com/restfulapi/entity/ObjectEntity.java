package com.restfulapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Persistence model representing a stored object.
 * - Plain data class with no Jackson annotations (decoupled from API contract)
 * - Fields: id, name, data map, createdAt, updatedAt
 * - Converted to/from DTOs via ObjectMapper
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectEntity {

    private String id;
    private String name;
    private Map<String, Object> data;
    private String createdAt;
    private String updatedAt;
}
