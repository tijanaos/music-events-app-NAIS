package rs.ac.uns.acs.nais.DynamicPricingService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("PriceSchedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceSchedule {

    @Id
    private String scheduleId;
    private String periodStart;
    private String periodEnd;
    private Double basePrice;
    private Double currentPrice;
    private Double minPrice;
    private Integer expectedSales;
    private Integer soldInPeriod;
    private Double priceStep;
}
