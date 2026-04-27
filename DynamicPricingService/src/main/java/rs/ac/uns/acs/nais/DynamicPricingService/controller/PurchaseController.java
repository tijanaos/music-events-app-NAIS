package rs.ac.uns.acs.nais.DynamicPricingService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.PurchaseRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.PurchaseResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.service.PurchaseService;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<List<PurchaseResponse>> getAll() {
        return ResponseEntity.ok(purchaseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(purchaseService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PurchaseResponse> create(@RequestBody PurchaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseResponse> update(@PathVariable String id, @RequestBody PurchaseRequest request) {
        return ResponseEntity.ok(purchaseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        purchaseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
