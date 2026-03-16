package com.restfulapi.repository;

import com.restfulapi.entity.ObjectEntity;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory implementation of ObjectRepository using ConcurrentHashMap.
 * - Thread-safe storage for ID generation
 * - Seeds 13 objects (IDs 1-13) on startup via @PostConstruct matching restful-api.dev data
 * - Swappable for JPA/Mongo by implementing the ObjectRepository interface
 */
@Repository
public class InMemoryObjectRepository implements ObjectRepository {

    private final Map<String, ObjectEntity> store = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    @PostConstruct
    void seedData() {
        seed("1", "Google Pixel 6 Pro", Map.of("color", "Cloudy White", "capacity", "128 GB"));
        seed("2", "Apple iPhone 12 Mini", Map.of("color", "Green", "capacity", "64 GB"));
        seed("3", "Apple iPhone 12 Pro Max", Map.of("color", "Pacific Blue", "capacity GB", 512));
        seed("4", "Apple iPhone 11, 64 GB", Map.of("price", 389.99, "color", "Purple"));
        seed("5", "Samsung Galaxy Z Fold2", Map.of("price", 689.99, "color", "Brown"));
        seed("6", "Apple AirPods", Map.of("generation", "3rd", "price", 120));
        seed("7", "Apple MacBook Pro 16", Map.of("year", 2019, "price", 1849.99, "CPU model", "Intel Core i9", "Hard disk size", "1 TB"));
        seed("8", "Apple Watch Series 8", Map.of("Strap Colour", "Elderberry", "Case Size", "41mm"));
        seed("9", "Beats Studio3 Wireless", Map.of("Color", "Red", "Description", "High-performance wireless noise cancelling headphones"));
        seed("10", "Apple iPad Mini 5th Gen", Map.of("Capacity", "64 GB", "Screen size", 7.9));
        seed("11", "Apple iPad Mini 5th Gen", Map.of("Capacity", "254 GB", "Screen size", 7.9));
        seed("12", "Apple AirPods", Map.of("generation", "1st", "price", 79));
        seed("13", "Apple iPad Air", Map.of("Generation", "4th", "Price", "419.99", "Capacity", "64 GB"));
        idSequence.set(13);
    }

    private void seed(String id, String name, Map<String, Object> data) {
        store.put(id, ObjectEntity.builder()
                .id(id)
                .name(name)
                .data(new LinkedHashMap<>(data))
                .build());
    }

    @Override
    public List<ObjectEntity> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<ObjectEntity> findByIds(List<String> ids) {
        return ids.stream()
                .map(store::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ObjectEntity> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public ObjectEntity save(ObjectEntity entity) {
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public boolean deleteById(String id) {
        return store.remove(id) != null;
    }

    @Override
    public String nextId() {
        return String.valueOf(idSequence.incrementAndGet());
    }
}
