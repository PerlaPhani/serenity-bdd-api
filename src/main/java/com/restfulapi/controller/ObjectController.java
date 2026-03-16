package com.restfulapi.controller;

import com.restfulapi.model.ApiObject;
import com.restfulapi.model.CreateObjectRequest;
import com.restfulapi.service.ObjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ObjectController {

    private final ObjectService objectService;

    public ObjectController(ObjectService objectService) {
        this.objectService = objectService;
    }

    @GetMapping("/objects")
    public ResponseEntity<?> getObjects(@RequestParam(name = "id", required = false) List<String> ids) {
        if (ids != null && !ids.isEmpty()) {
            return ResponseEntity.ok(objectService.getByIds(ids));
        }
        return ResponseEntity.ok(objectService.getAll());
    }

    @GetMapping("/objects/{id}")
    public ResponseEntity<?> getObjectById(@PathVariable String id) {
        return objectService.getById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(Map.of("error", "Oops! Object with id=" + id + " was not found.")));
    }

    @PostMapping("/objects")
    public ResponseEntity<ApiObject> createObject(@RequestBody CreateObjectRequest request) {
        return ResponseEntity.ok(objectService.create(request));
    }

    @PutMapping("/objects/{id}")
    public ResponseEntity<?> updateObject(@PathVariable String id, @RequestBody CreateObjectRequest request) {
        return objectService.update(id, request)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(Map.of("error", "Oops! Object with id=" + id + " was not found.")));
    }

    @PatchMapping("/objects/{id}")
    public ResponseEntity<?> partialUpdateObject(@PathVariable String id, @RequestBody CreateObjectRequest request) {
        return objectService.partialUpdate(id, request)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(Map.of("error", "Oops! Object with id=" + id + " was not found.")));
    }

    @DeleteMapping("/objects/{id}")
    public ResponseEntity<?> deleteObject(@PathVariable String id) {
        if (objectService.delete(id)) {
            return ResponseEntity.ok(Map.of("message", "Object with id = " + id + " has been deleted."));
        }
        return ResponseEntity.status(404)
                .body(Map.of("error", "Oops! Object with id=" + id + " was not found."));
    }
}
