package rs.ac.uns.acs.nais.DynamicPricingService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics.*;
import rs.ac.uns.acs.nais.DynamicPricingService.exception.ResourceNotFoundException;
import rs.ac.uns.acs.nais.DynamicPricingService.repository.AnalyticsQueryRepository;
import rs.ac.uns.acs.nais.DynamicPricingService.repository.CustomerRepository;
import rs.ac.uns.acs.nais.DynamicPricingService.service.AnalyticsService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final CustomerRepository customerRepository;
    private final AnalyticsQueryRepository analyticsQueryRepository;

    @Override
    public List<TicketRecommendationResponse> getTicketRecommendations(String customerId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
        return analyticsQueryRepository.findTicketRecommendations(customerId, LocalDate.now().toString());
    }

    @Override
    public List<TicketPricingRecommendationResponse> getPricingRecommendations(int hours) {
        String today = LocalDate.now().toString();
        String windowStart = LocalDate.now().minusDays(hours / 24).toString();
        return analyticsQueryRepository.findPricingRecommendations(today, windowStart);
    }

    @Override
    public List<PromoCodeEffectivenessResponse> getPromoCodeEffectiveness() {
        return analyticsQueryRepository.findPromoCodeEffectiveness();
    }

    @Override
    public List<CustomerSpendingResponse> getCustomerSpendingProfiles() {
        return analyticsQueryRepository.findCustomerSpendingProfiles();
    }

    @Override
    public List<PriceScheduleTrendResponse> getPriceScheduleTrend() {
        return analyticsQueryRepository.findPriceScheduleTrend();
    }
}
