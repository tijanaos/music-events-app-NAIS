package rs.ac.uns.acs.nais.NegotiationAnalyticsService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.model.OfferLifecycleEvent;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.service.OfferEventService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/offer-events")
public class OfferEventController {

    private final OfferEventService service;

    public OfferEventController(OfferEventService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Boolean> save(@RequestBody OfferLifecycleEvent record) {
        return ResponseEntity.ok(service.save(record));
    }

    @GetMapping("/{offerId}")
    public ResponseEntity<List<OfferLifecycleEvent>> findByOfferId(@PathVariable String offerId) {
        return ResponseEntity.ok(service.findByOfferId(offerId));
    }

    @DeleteMapping("/{offerId}")
    public ResponseEntity<Boolean> deleteByOfferId(@PathVariable String offerId) {
        return ResponseEntity.ok(service.deleteByOfferId(offerId));
    }

    @GetMapping("/analytics/avg-offer-price")
    public ResponseEntity<List<Map<String, Object>>> avgOfferPrice() {
        return ResponseEntity.ok(service.avgOfferPriceByEventTypeAndLocation());
    }
}
