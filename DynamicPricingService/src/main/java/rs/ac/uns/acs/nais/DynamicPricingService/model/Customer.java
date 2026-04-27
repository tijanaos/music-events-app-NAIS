package rs.ac.uns.acs.nais.DynamicPricingService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import rs.ac.uns.acs.nais.DynamicPricingService.model.enums.CustomerTier;
import rs.ac.uns.acs.nais.DynamicPricingService.model.relationship.MadePurchase;

import java.util.ArrayList;
import java.util.List;

@Node("Customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    private String customerId;
    private String name;
    private String email;
    private CustomerTier tier;

    @Relationship(type = "MADE_PURCHASE", direction = Relationship.Direction.OUTGOING)
    private List<MadePurchase> purchases = new ArrayList<>();
}
