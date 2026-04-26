package rs.ac.uns.acs.nais.PerformerManagementService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.PerformerManagementService.dto.PerformerDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.model.Performer;
import rs.ac.uns.acs.nais.PerformerManagementService.service.IPerformerService;

import java.util.List;

@RestController
@RequestMapping("/api/performers")
@RequiredArgsConstructor
@Tag(name = "Performer", description = "Performer management endpoints")
public class PerformerController {

    private final IPerformerService performerService;

    @GetMapping
    @Operation(summary = "Get all performers")
    public ResponseEntity<List<Performer>> getAll() {
        return ResponseEntity.ok(performerService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get performer by ID")
    public ResponseEntity<Performer> getById(@PathVariable String id) {
        return ResponseEntity.ok(performerService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new performer")
    public ResponseEntity<Performer> create(@RequestBody PerformerDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(performerService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing performer")
    public ResponseEntity<Performer> update(@PathVariable String id, @RequestBody PerformerDTO dto) {
        return ResponseEntity.ok(performerService.update(id, dto));
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive a performer (soft delete)")
    public ResponseEntity<Performer> archive(@PathVariable String id) {
        return ResponseEntity.ok(performerService.archive(id));
    }

    @GetMapping("/by-genre/{genre}")
    @Operation(summary = "Get performers by genre")
    public ResponseEntity<List<Performer>> getByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(performerService.findByGenre(genre));
    }

    @GetMapping("/by-country/{country}")
    @Operation(summary = "Get performers by country of origin")
    public ResponseEntity<List<Performer>> getByCountry(@PathVariable String country) {
        return ResponseEntity.ok(performerService.findByCountry(country));
    }

    @GetMapping("/by-member-count/{count}")
    @Operation(summary = "Get performers by number of members")
    public ResponseEntity<List<Performer>> getByMemberCount(@PathVariable Integer count) {
        return ResponseEntity.ok(performerService.findByMemberCount(count));
    }
}
