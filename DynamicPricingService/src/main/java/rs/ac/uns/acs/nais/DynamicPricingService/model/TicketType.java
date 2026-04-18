package rs.ac.uns.acs.nais.DynamicPricingService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("TicketType")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketType {

    @Id
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
