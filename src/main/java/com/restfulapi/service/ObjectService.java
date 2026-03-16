package com.restfulapi.service;

import com.restfulapi.dto.CreateObjectRequest;
import com.restfulapi.dto.ObjectResponse;
import com.restfulapi.entity.ObjectEntity;
import com.restfulapi.exception.ObjectNotFoundException;
import com.restfulapi.mapper.ObjectMapper;
import com.restfulapi.repository.ObjectRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Business logic layer for object CRUD operations.
 * - Orchestrates repository calls and entity-to-DTO mapping
 * - Throws ObjectNotFoundException for missing objects mapped to 404 by GlobalExceptionHandler
 * - Handles full update (PUT), partial merge update (PATCH), and delete
 * - Sets createdAt on create and updatedAt on update/patch
 */
@Service
public class ObjectService {

    private final ObjectRepository repository;

    public ObjectService(ObjectRepository repository) {
        this.repository = repository;
    }

    public List<ObjectResponse> getAll() {
        return ObjectMapper.toResponseList(repository.findAll());
    }

    public List<ObjectResponse> getByIds(List<String> ids) {
        return ObjectMapper.toResponseList(repository.findByIds(ids));
    }

    public ObjectResponse getById(String id) {
        ObjectEntity entity = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id));
        return ObjectMapper.toResponse(entity);
    }

    public ObjectResponse create(CreateObjectRequest request) {
        String id = repository.nextId();
        ObjectEntity entity = ObjectMapper.toEntity(id, request);
        entity.setCreatedAt(Instant.now().toString());
        return ObjectMapper.toResponse(repository.save(entity));
    }

    public ObjectResponse update(String id, CreateObjectRequest request) {
        ObjectEntity existing = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id));
        ObjectEntity updated = ObjectEntity.builder()
                .id(id)
                .name(request.getName())
                .data(request.getData())
                .createdAt(existing.getCreatedAt())
                .updatedAt(Instant.now().toString())
                .build();
        return ObjectMapper.toResponse(repository.save(updated));
    }

    public ObjectResponse partialUpdate(String id, CreateObjectRequest request) {
        ObjectEntity existing = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id));

        String name = request.getName() != null ? request.getName() : existing.getName();

        Map<String, Object> mergedData = new LinkedHashMap<>();
        if (existing.getData() != null) {
            mergedData.putAll(existing.getData());
        }
        if (request.getData() != null) {
            mergedData.putAll(request.getData());
        }

        ObjectEntity patched = ObjectEntity.builder()
                .id(id)
                .name(name)
                .data(mergedData.isEmpty() ? null : mergedData)
                .createdAt(existing.getCreatedAt())
                .updatedAt(Instant.now().toString())
                .build();
        return ObjectMapper.toResponse(repository.save(patched));
    }

    public void delete(String id) {
        if (!repository.deleteById(id)) {
            throw new ObjectNotFoundException(id);
        }
    }
}
