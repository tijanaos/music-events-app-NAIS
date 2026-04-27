package rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics;

public record TierUpgradeResponse(
        String customerId,
        String name,
        String oldTier,
        String newTier,
        Double totalSpent
) {}
