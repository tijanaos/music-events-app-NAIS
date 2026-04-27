package rs.ac.uns.acs.nais.DynamicPricingService.service;

import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics.CustomerSpendingResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics.PriceScheduleTrendResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics.PromoCodeEffectivenessResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics.TicketPricingRecommendationResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics.TicketRecommendationResponse;

import java.util.List;

public interface AnalyticsService {

    List<TicketRecommendationResponse> getTicketRecommendations(String customerId);

    List<TicketPricingRecommendationResponse> getPricingRecommendations(int hours);

    List<PromoCodeEffectivenessResponse> getPromoCodeEffectiveness();

    List<CustomerSpendingResponse> getCustomerSpendingProfiles();

    List<PriceScheduleTrendResponse> getPriceScheduleTrend();
}
