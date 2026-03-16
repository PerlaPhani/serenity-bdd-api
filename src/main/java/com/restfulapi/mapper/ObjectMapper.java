package com.restfulapi.mapper;

import com.restfulapi.dto.CreateObjectRequest;
import com.restfulapi.dto.ObjectResponse;
import com.restfulapi.entity.ObjectEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Static utility for converting between ObjectEntity and DTOs.
 * - toResponse: converts entity to ObjectResponse DTO
 * - toResponseList: batch converts a list of entities
 * - toEntity: creates an entity from an ID and CreateObjectRequest
 */
public class ObjectMapper {

    private ObjectMapper() {}

    public static ObjectResponse toResponse(ObjectEntity entity) {
        return ObjectResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .data(entity.getData())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static List<ObjectResponse> toResponseList(List<ObjectEntity> entities) {
        return entities.stream()
                .map(ObjectMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static ObjectEntity toEntity(String id, CreateObjectRequest request) {
        return ObjectEntity.builder()
                .id(id)
                .name(request.getName())
                .data(request.getData())
                .build();
    }
}
