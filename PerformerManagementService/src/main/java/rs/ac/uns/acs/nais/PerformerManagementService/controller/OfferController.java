package rs.ac.uns.acs.nais.PerformerManagementService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.PerformerManagementService.dto.OfferDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.model.Offer;
import rs.ac.uns.acs.nais.PerformerManagementService.model.enums.OfferStatus;
import rs.ac.uns.acs.nais.PerformerManagementService.service.IOfferService;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
@Tag(name = "Offer", description = "Offer management endpoints")
public class OfferController {

    private final IOfferService offerService;

    @GetMapping
    @Operation(summary = "Get all offers")
    public ResponseEntity<List<Offer>> getAll() {
        return ResponseEntity.ok(offerService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get offer by ID")
    public ResponseEntity<Offer> getById(@PathVariable String id) {
        return ResponseEntity.ok(offerService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new offer")
    public ResponseEntity<Offer> create(@RequestBody OfferDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(offerService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an offer (only in CREATED status)")
    public ResponseEntity<Offer> update(@PathVariable String id, @RequestBody OfferDTO dto) {
        return ResponseEntity.ok(offerService.update(id, dto));
    }

    @PatchMapping("/{id}/publish")
    @Operation(summary = "Publish an offer")
    public ResponseEntity<Offer> publish(@PathVariable String id) {
        return ResponseEntity.ok(offerService.publish(id));
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive an offer")
    public ResponseEntity<Offer> archive(@PathVariable String id) {
        return ResponseEntity.ok(offerService.archive(id));
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "Get offers by status")
    public ResponseEntity<List<Offer>> getByStatus(@PathVariable OfferStatus status) {
        return ResponseEntity.ok(offerService.findByStatus(status));
    }
}
