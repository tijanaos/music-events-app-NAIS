package rs.ac.uns.acs.nais.DynamicPricingService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.PriceScheduleRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.PriceScheduleResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.service.PriceScheduleService;

import java.util.List;

@RestController
@RequestMapping("/api/price-schedules")
@RequiredArgsConstructor
public class PriceScheduleController {

    private final PriceScheduleService priceScheduleService;

    @GetMapping
    public ResponseEntity<List<PriceScheduleResponse>> getAll() {
        return ResponseEntity.ok(priceScheduleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriceScheduleResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(priceScheduleService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PriceScheduleResponse> create(@RequestBody PriceScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(priceScheduleService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PriceScheduleResponse> update(@PathVariable String id, @RequestBody PriceScheduleRequest request) {
        return ResponseEntity.ok(priceScheduleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        priceScheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
