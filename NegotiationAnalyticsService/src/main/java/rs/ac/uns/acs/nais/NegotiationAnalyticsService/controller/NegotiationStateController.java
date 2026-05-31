package rs.ac.uns.acs.nais.NegotiationAnalyticsService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.model.NegotiationStateHistory;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.service.NegotiationStateService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/negotiation-states")
public class NegotiationStateController {

    private final NegotiationStateService service;

    public NegotiationStateController(NegotiationStateService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Boolean> save(@RequestBody NegotiationStateHistory record) {
        return ResponseEntity.ok(service.save(record));
    }

    @GetMapping("/{negotiationId}")
    public ResponseEntity<List<NegotiationStateHistory>> findByNegotiationId(@PathVariable String negotiationId) {
        return ResponseEntity.ok(service.findByNegotiationId(negotiationId));
    }

    @DeleteMapping("/{negotiationId}")
    public ResponseEntity<Boolean> deleteByNegotiationId(@PathVariable String negotiationId) {
        return ResponseEntity.ok(service.deleteByNegotiationId(negotiationId));
    }

    @GetMapping("/analytics/bottlenecks")
    public ResponseEntity<List<Map<String, Object>>> bottlenecksByTemplate(
            @RequestParam String templateName) {
        return ResponseEntity.ok(service.avgDurationPerStateByTemplate(templateName));
    }

    @GetMapping("/analytics/monthly-trend")
    public ResponseEntity<List<Map<String, Object>>> monthlyTrend() {
        return ResponseEntity.ok(service.monthlyNegotiationSuccessTrend());
    }
}
