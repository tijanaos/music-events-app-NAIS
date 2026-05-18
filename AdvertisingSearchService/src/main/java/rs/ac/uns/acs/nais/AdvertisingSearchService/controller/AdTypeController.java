package rs.ac.uns.acs.nais.AdvertisingSearchService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.request.AdTypeRequest;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdTypeResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.service.AdTypeService;

import java.util.List;

@RestController
@RequestMapping("/api/ad-types")
@RequiredArgsConstructor
public class AdTypeController {

    private final AdTypeService adTypeService;

    @GetMapping
    public ResponseEntity<List<AdTypeResponse>> getAll() {
        return ResponseEntity.ok(adTypeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdTypeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(adTypeService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AdTypeResponse> create(@Valid @RequestBody AdTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adTypeService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdTypeResponse> update(@PathVariable Long id, @Valid @RequestBody AdTypeRequest request) {
        return ResponseEntity.ok(adTypeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
