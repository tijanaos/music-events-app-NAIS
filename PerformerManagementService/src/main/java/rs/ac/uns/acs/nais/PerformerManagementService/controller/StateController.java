package rs.ac.uns.acs.nais.PerformerManagementService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.PerformerManagementService.dto.StateDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.model.State;
import rs.ac.uns.acs.nais.PerformerManagementService.service.IStateService;

import java.util.List;

@RestController
@RequestMapping("/api/states")
@RequiredArgsConstructor
@Tag(name = "State", description = "Workflow state management endpoints")
public class StateController {

    private final IStateService stateService;

    @GetMapping
    @Operation(summary = "Get all states")
    public ResponseEntity<List<State>> getAll() {
        return ResponseEntity.ok(stateService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get state by ID")
    public ResponseEntity<State> getById(@PathVariable String id) {
        return ResponseEntity.ok(stateService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new state")
    public ResponseEntity<State> create(@RequestBody StateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stateService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a state")
    public ResponseEntity<State> update(@PathVariable String id, @RequestBody StateDTO dto) {
        return ResponseEntity.ok(stateService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a state")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        stateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
