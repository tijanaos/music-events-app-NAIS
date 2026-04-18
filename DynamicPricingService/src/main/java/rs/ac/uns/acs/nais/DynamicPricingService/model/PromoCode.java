package rs.ac.uns.acs.nais.DynamicPricingService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import rs.ac.uns.acs.nais.DynamicPricingService.model.relationship.ValidFor;

import java.util.ArrayList;
import java.util.List;

@Node("PromoCode")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromoCode {

    @Id
    private String code;
    private Integer discountPercent;
    private String validFrom;
    private String validTo;
    private Integer maxUses;
    private Integer currentUses;

    @Relationship(type = "VALID_FOR", direction = Relationship.Direction.OUTGOING)
    private List<ValidFor> validForTickets = new ArrayList<>();
}
