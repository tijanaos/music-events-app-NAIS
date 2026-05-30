package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request.ResourceUsageDto;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ResourceUsageResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service.ResourceUsageService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/resource-usage")
@RequiredArgsConstructor
public class ResourceUsageController {

    private final ResourceUsageService service;

    @PostMapping
    public ResponseEntity<ResourceUsageResponse> create(@Valid @RequestBody ResourceUsageDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceUsageResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<ResourceUsageResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/by-stage/{stageId}")
    public ResponseEntity<List<ResourceUsageResponse>> findByStage(@PathVariable String stageId) {
        return ResponseEntity.ok(service.findByStageId(stageId));
    }

    @GetMapping("/by-resource/{resourceId}")
    public ResponseEntity<List<ResourceUsageResponse>> findByResource(@PathVariable String resourceId) {
        return ResponseEntity.ok(service.findByResourceId(resourceId));
    }

    @GetMapping("/by-type")
    public ResponseEntity<List<ResourceUsageResponse>> findByType(@RequestParam String type) {
        return ResponseEntity.ok(service.findByResourceType(type));
    }

    @GetMapping("/by-period")
    public ResponseEntity<List<ResourceUsageResponse>> findByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(service.findByPeriod(from, to));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<List<ResourceUsageResponse>> findBorrowedResources() {
        return ResponseEntity.ok(service.findBorrowedResources());
    }

    @GetMapping("/by-reservation/{reservationId}")
    public ResponseEntity<List<ResourceUsageResponse>> findByReservation(@PathVariable String reservationId) {
        return ResponseEntity.ok(service.findByReservationId(reservationId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceUsageResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ResourceUsageDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
