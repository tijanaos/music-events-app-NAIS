package rs.ac.uns.acs.nais.DynamicPricingService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.PromoCodeRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.PromoCodeResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.service.PromoCodeService;

import java.util.List;

@RestController
@RequestMapping("/api/promo-codes")
@RequiredArgsConstructor
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    @GetMapping
    public ResponseEntity<List<PromoCodeResponse>> getAll() {
        return ResponseEntity.ok(promoCodeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromoCodeResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(promoCodeService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PromoCodeResponse> create(@RequestBody PromoCodeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(promoCodeService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromoCodeResponse> update(@PathVariable String id, @RequestBody PromoCodeRequest request) {
        return ResponseEntity.ok(promoCodeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        promoCodeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
