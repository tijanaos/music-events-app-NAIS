package rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics;

import java.util.List;

public record CustomerSpendingResponse(
        String customerId,
        String name,
        String tier,
        Long purchaseCount,
        Double totalSpent,
        Double totalDiscount,
        Double effectiveDiscountRate,
        Long promosUsed,
        List<String> ticketTypesBought
) {}
