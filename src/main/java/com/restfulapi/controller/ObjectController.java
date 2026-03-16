package com.restfulapi.controller;

import com.restfulapi.dto.CreateObjectRequest;
import com.restfulapi.dto.ObjectResponse;
import com.restfulapi.service.ObjectService;
import jakarta.validation.Valid;
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

/**
 * REST controller for /objects endpoints.
 * - Delegates all business logic to ObjectService - single dependency, constructor-injected
 * - GET /objects — list all objects, with optional ?id= query param filtering
 * - GET /objects/{id} — retrieve a single object by ID
 * - POST /objects — create a new object (@Valid request body)
 * - PUT /objects/{id} — full replacement update (@Valid request body)
 * - PATCH /objects/{id} — partial merge update (no @Valid, allows partial payloads)
 * - DELETE /objects/{id} — delete by ID, returns a confirmation message map
 * - Bean validation via @Valid on POST and PUT
 * - Returns ResponseEntity wrappers using DTOs (ObjectResponse, CreateObjectRequest)
 */
@RestController
public class ObjectController {
    private final ObjectService objectService;
    public ObjectController(ObjectService objectService) {
        this.objectService = objectService;
    }

    @GetMapping("/objects")
    public ResponseEntity<List<ObjectResponse>> getObjects(@RequestParam(name = "id", required = false) List<String> ids) {
        if (ids != null && !ids.isEmpty()) {
            return ResponseEntity.ok(objectService.getByIds(ids));
        }
        return ResponseEntity.ok(objectService.getAll());
    }

    @GetMapping("/objects/{id}")
    public ResponseEntity<ObjectResponse> getObjectById(@PathVariable String id) {
        return ResponseEntity.ok(objectService.getById(id));
    }

    @PostMapping("/objects")
    public ResponseEntity<ObjectResponse> createObject(@Valid @RequestBody CreateObjectRequest request) {
        return ResponseEntity.ok(objectService.create(request));
    }

    @PutMapping("/objects/{id}")
    public ResponseEntity<ObjectResponse> updateObject(@PathVariable String id, @Valid @RequestBody CreateObjectRequest request) {
        return ResponseEntity.ok(objectService.update(id, request));
    }

    @PatchMapping("/objects/{id}")
    public ResponseEntity<ObjectResponse> partialUpdateObject(@PathVariable String id, @RequestBody CreateObjectRequest request) {
        return ResponseEntity.ok(objectService.partialUpdate(id, request));
    }

    @DeleteMapping("/objects/{id}")
    public ResponseEntity<Map<String, String>> deleteObject(@PathVariable String id) {
        objectService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Object with id = " + id + " has been deleted."));
    }
}
