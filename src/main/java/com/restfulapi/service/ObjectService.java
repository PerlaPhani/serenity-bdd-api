package com.restfulapi.service;

import com.restfulapi.model.ApiObject;
import com.restfulapi.model.CreateObjectRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ObjectService {

    private final Map<String, ApiObject> store = new ConcurrentHashMap<>();
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
        store.put(id, ApiObject.builder()
                .id(id)
                .name(name)
                .data(new LinkedHashMap<>(data))
                .build());
    }

    public List<ApiObject> getAll() {
        return new ArrayList<>(store.values());
    }

    public List<ApiObject> getByIds(List<String> ids) {
        return ids.stream()
                .map(store::get)
                .filter(obj -> obj != null)
                .collect(Collectors.toList());
    }

    public Optional<ApiObject> getById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public ApiObject create(CreateObjectRequest request) {
        String id = String.valueOf(idSequence.incrementAndGet());
        ApiObject obj = ApiObject.builder()
                .id(id)
                .name(request.getName())
                .data(request.getData())
                .createdAt(Instant.now().toString())
                .build();
        store.put(id, obj);
        return obj;
    }

    public Optional<ApiObject> update(String id, CreateObjectRequest request) {
        ApiObject existing = store.get(id);
        if (existing == null) {
            return Optional.empty();
        }
        ApiObject updated = ApiObject.builder()
                .id(id)
                .name(request.getName())
                .data(request.getData())
                .updatedAt(Instant.now().toString())
                .build();
        store.put(id, updated);
        return Optional.of(updated);
    }

    public Optional<ApiObject> partialUpdate(String id, CreateObjectRequest request) {
        ApiObject existing = store.get(id);
        if (existing == null) {
            return Optional.empty();
        }

        String name = request.getName() != null ? request.getName() : existing.getName();

        Map<String, Object> mergedData = new LinkedHashMap<>();
        if (existing.getData() != null) {
            mergedData.putAll(existing.getData());
        }
        if (request.getData() != null) {
            mergedData.putAll(request.getData());
        }

        ApiObject patched = ApiObject.builder()
                .id(id)
                .name(name)
                .data(mergedData.isEmpty() ? null : mergedData)
                .createdAt(existing.getCreatedAt())
                .updatedAt(Instant.now().toString())
                .build();
        store.put(id, patched);
        return Optional.of(patched);
    }

    public boolean delete(String id) {
        return store.remove(id) != null;
    }
}
