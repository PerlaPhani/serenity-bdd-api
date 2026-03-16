package com.restfulapi.repository;

import com.restfulapi.entity.ObjectEntity;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface abstracting object storage operations.
 * - Defines CRUD methods: findAll, findByIds, findById, save, deleteById
 * - Provides nextId() for sequential ID generation
 */
public interface ObjectRepository {

    List<ObjectEntity> findAll();

    List<ObjectEntity> findByIds(List<String> ids);

    Optional<ObjectEntity> findById(String id);

    ObjectEntity save(ObjectEntity entity);

    boolean deleteById(String id);

    String nextId();
}
