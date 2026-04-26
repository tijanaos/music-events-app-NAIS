package rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics;

import java.util.List;

public record PromoCodeEffectivenessResponse(
        String promoCodeId,
        Long discountPercent,
        String validFrom,
        String validTo,
        Long maxUses,
        Long usageCount,
        Long uniqueBuyers,
        Double totalDiscount,
        Double utilizationRate,
        List<String> validForTickets
) {}
