package rs.ac.uns.acs.nais.GraphDatabaseService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ResourceDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Resource;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ResourceType;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.IResourceService;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@Tag(name = "Resource", description = "Resource management endpoints")
public class ResourceController {

    private final IResourceService resourceService;

    @GetMapping
    @Operation(summary = "Get all resources")
    public ResponseEntity<List<Resource>> getAll() {
        return ResponseEntity.ok(resourceService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get resource by ID")
    public ResponseEntity<Resource> getById(@PathVariable String id) {
        return ResponseEntity.ok(resourceService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new resource")
    public ResponseEntity<Resource> create(@RequestBody ResourceDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing resource")
    public ResponseEntity<Resource> update(@PathVariable String id, @RequestBody ResourceDTO dto) {
        return ResponseEntity.ok(resourceService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a resource")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        resourceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-type/{type}")
    @Operation(summary = "Get resources by type")
    public ResponseEntity<List<Resource>> getByType(@PathVariable ResourceType type) {
        return ResponseEntity.ok(resourceService.findByType(type));
    }

    @GetMapping("/portable")
    @Operation(summary = "Get all portable resources")
    public ResponseEntity<List<Resource>> getPortable() {
        return ResponseEntity.ok(resourceService.findByPortable(true));
    }
}
