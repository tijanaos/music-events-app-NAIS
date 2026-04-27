package rs.ac.uns.acs.nais.DynamicPricingService.dto.response.analytics;

public record PriceScheduleTrendResponse(
        String ticketTypeId,
        String ticketName,
        String scheduleId,
        String periodStart,
        String periodEnd,
        Double basePrice,
        Double currentPrice,
        Double priceChange,
        Long totalSales,
        Long platformSales
) {}
