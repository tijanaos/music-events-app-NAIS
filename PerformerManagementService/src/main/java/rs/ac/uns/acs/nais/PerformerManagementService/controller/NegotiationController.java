package rs.ac.uns.acs.nais.PerformerManagementService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.PerformerManagementService.dto.NegotiationDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.model.Negotiation;
import rs.ac.uns.acs.nais.PerformerManagementService.service.INegotiationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/negotiations")
@RequiredArgsConstructor
@Tag(name = "Negotiation", description = "Negotiation management and analytics endpoints")
public class NegotiationController {

    private final INegotiationService negotiationService;

    @GetMapping
    @Operation(summary = "Get all negotiations")
    public ResponseEntity<List<Negotiation>> getAll() {
        return ResponseEntity.ok(negotiationService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get negotiation by ID")
    public ResponseEntity<Negotiation> getById(@PathVariable String id) {
        return ResponseEntity.ok(negotiationService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new negotiation from an offer")
    public ResponseEntity<Negotiation> create(@RequestBody NegotiationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(negotiationService.create(dto));
    }

    @PatchMapping("/{id}/advance")
    @Operation(summary = "Advance negotiation to a new state")
    public ResponseEntity<Negotiation> advanceState(
            @PathVariable String id,
            @RequestParam String newStateId) {
        return ResponseEntity.ok(negotiationService.advanceState(id, newStateId));
    }

    @PatchMapping("/{id}/conclude")
    @Operation(summary = "Successfully conclude a negotiation (must be in final state)")
    public ResponseEntity<Negotiation> conclude(@PathVariable String id) {
        return ResponseEntity.ok(negotiationService.conclude(id));
    }

    @PatchMapping("/{id}/fail")
    @Operation(summary = "Fail a negotiation with a reason")
    public ResponseEntity<Negotiation> fail(
            @PathVariable String id,
            @RequestParam String failReason) {
        return ResponseEntity.ok(negotiationService.fail(id, failReason));
    }

    @GetMapping("/by-manager/{manager}")
    @Operation(summary = "Get negotiations by manager username")
    public ResponseEntity<List<Negotiation>> getByManager(@PathVariable String manager) {
        return ResponseEntity.ok(negotiationService.findByCreatedBy(manager));
    }

    @GetMapping("/analytics/manager-stats")
    @Operation(summary = "Q1: Per-manager negotiation count and success rate")
    public ResponseEntity<List<Map<String, Object>>> getManagerStats() {
        return ResponseEntity.ok(negotiationService.getManagerStats());
    }

    @GetMapping("/analytics/performer-success")
    @Operation(summary = "Q2: Performers ranked by number of concluded negotiations")
    public ResponseEntity<List<Map<String, Object>>> getPerformerSuccess() {
        return ResponseEntity.ok(negotiationService.getPerformerSuccessStats());
    }

    @GetMapping("/analytics/offer-stats")
    @Operation(summary = "Q3: Per-offer negotiation funnel (total, concluded, failed)")
    public ResponseEntity<List<Map<String, Object>>> getOfferStats() {
        return ResponseEntity.ok(negotiationService.getOfferStats());
    }

    @GetMapping("/analytics/genre-stats")
    @Operation(summary = "Q4: Average agreed fee and negotiation count per genre")
    public ResponseEntity<List<Map<String, Object>>> getGenreStats() {
        return ResponseEntity.ok(negotiationService.getGenreStats());
    }

    @GetMapping("/analytics/stagnant")
    @Operation(summary = "Q5: Negotiations stagnating beyond allowed state duration")
    public ResponseEntity<List<Map<String, Object>>> getStagnant() {
        return ResponseEntity.ok(negotiationService.getStagnantNegotiations());
    }
}
