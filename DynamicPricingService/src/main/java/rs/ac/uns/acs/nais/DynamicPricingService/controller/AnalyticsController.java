package rs.ac.uns.acs.nais.DynamicPricingService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics.CustomerSpendingResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics.PriceScheduleTrendResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics.PromoCodeEffectivenessResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics.TicketPricingRecommendationResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics.TicketRecommendationResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.service.AnalyticsService;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/recommendations/{customerId}")
    public ResponseEntity<List<TicketRecommendationResponse>> getTicketRecommendations(
            @PathVariable String customerId) {
        return ResponseEntity.ok(analyticsService.getTicketRecommendations(customerId));
    }

    @GetMapping("/pricing-recommendations")
    public ResponseEntity<List<TicketPricingRecommendationResponse>> getPricingRecommendations(
            @RequestParam(defaultValue = "48") int hours) {
        return ResponseEntity.ok(analyticsService.getPricingRecommendations(hours));
    }

    @GetMapping("/promo-effectiveness")
    public ResponseEntity<List<PromoCodeEffectivenessResponse>> getPromoCodeEffectiveness() {
        return ResponseEntity.ok(analyticsService.getPromoCodeEffectiveness());
    }

    @GetMapping("/customer-spending")
    public ResponseEntity<List<CustomerSpendingResponse>> getCustomerSpendingProfiles() {
        return ResponseEntity.ok(analyticsService.getCustomerSpendingProfiles());
    }

    @GetMapping("/price-trend")
    public ResponseEntity<List<PriceScheduleTrendResponse>> getPriceScheduleTrend() {
        return ResponseEntity.ok(analyticsService.getPriceScheduleTrend());
    }
}
