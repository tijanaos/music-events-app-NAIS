package rs.ac.uns.acs.nais.GraphDatabaseService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.StageDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Stage;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.StageType;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.IStageService;

import java.util.List;

@RestController
@RequestMapping("/api/stages")
@RequiredArgsConstructor
@Tag(name = "Stage", description = "Stage management endpoints")
public class StageController {

    private final IStageService stageService;

    @GetMapping
    @Operation(summary = "Get all stages")
    public ResponseEntity<List<Stage>> getAll() {
        return ResponseEntity.ok(stageService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get stage by ID")
    public ResponseEntity<Stage> getById(@PathVariable String id) {
        return ResponseEntity.ok(stageService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new stage")
    public ResponseEntity<Stage> create(@RequestBody StageDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stageService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing stage")
    public ResponseEntity<Stage> update(@PathVariable String id, @RequestBody StageDTO dto) {
        return ResponseEntity.ok(stageService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a stage")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        stageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-type/{type}")
    @Operation(summary = "Get stages by type")
    public ResponseEntity<List<Stage>> getByType(@PathVariable StageType type) {
        return ResponseEntity.ok(stageService.findByType(type));
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active stages")
    public ResponseEntity<List<Stage>> getActive() {
        return ResponseEntity.ok(stageService.findByActive(true));
    }
}
