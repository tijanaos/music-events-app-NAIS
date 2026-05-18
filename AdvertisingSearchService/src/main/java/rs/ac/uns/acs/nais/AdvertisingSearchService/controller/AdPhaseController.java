package rs.ac.uns.acs.nais.AdvertisingSearchService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.request.AdPhaseRequest;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdPhaseResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.service.AdPhaseService;

import java.util.List;

@RestController
@RequestMapping("/api/ad-phases")
@RequiredArgsConstructor
public class AdPhaseController {

    private final AdPhaseService adPhaseService;

    @GetMapping
    public ResponseEntity<List<AdPhaseResponse>> getAll() {
        return ResponseEntity.ok(adPhaseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdPhaseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(adPhaseService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AdPhaseResponse> create(@Valid @RequestBody AdPhaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adPhaseService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdPhaseResponse> update(@PathVariable Long id, @Valid @RequestBody AdPhaseRequest request) {
        return ResponseEntity.ok(adPhaseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adPhaseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
