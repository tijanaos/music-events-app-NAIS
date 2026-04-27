package rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics;

public record TicketRecommendationResponse(
        String ticketTypeId,
        String name,
        Double currentPrice,
        Long recommendedBy,
        Long maxOverlap
) {}
