package rs.ac.uns.acs.nais.DynamicPricingService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Purchase")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {

    @Id
    private String purchaseId;
    private String date;
    private Integer quantity;
    private Double unitPrice;
    private Double tierDiscountApplied;
    private Double promoDiscountApplied;
    private Double finalPrice;

    @Relationship(type = "FOR_TICKET", direction = Relationship.Direction.OUTGOING)
    private TicketType ticketType;

    @Relationship(type = "USED_PROMO", direction = Relationship.Direction.OUTGOING)
    private PromoCode promoCode;
}
