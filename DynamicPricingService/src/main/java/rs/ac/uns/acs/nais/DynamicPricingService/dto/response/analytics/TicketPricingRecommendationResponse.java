package rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics;

public record TicketPricingRecommendationResponse(
        String ticketTypeId,
        String name,
        Double currentPrice,
        Double basePrice,
        Double minPrice,
        Double priceStep,
        Long soldCount,
        Long maxAvailable,
        Long recentSales,
        Long expectedSales,
        Double sellThrough,
        Double capacityRatio,
        String action
) {}
