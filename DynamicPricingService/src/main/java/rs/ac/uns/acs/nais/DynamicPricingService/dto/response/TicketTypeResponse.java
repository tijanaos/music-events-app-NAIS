package rs.ac.uns.acs.nais.DynamicPricingService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketTypeResponse {

    private String ticketTypeId;
    private String name;
    private Double basePrice;
    private Double minPrice;
    private Double currentPrice;
    private Integer maxAvailable;
    private Integer soldCount;
    private Integer expectedSales;
    private String periodStart;
    private String periodEnd;
}
