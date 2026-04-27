package rs.ac.uns.acs.nais.DynamicPricingService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceScheduleRequest {

    private String scheduleId;
    private String periodStart;
    private String periodEnd;
    private Double basePrice;
    private Double currentPrice;
    private Double minPrice;
    private Integer expectedSales;
    private Integer soldInPeriod;
    private Double priceStep;
    private String ticketTypeId;
}
