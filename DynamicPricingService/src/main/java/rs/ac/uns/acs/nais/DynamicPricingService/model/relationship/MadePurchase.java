package rs.ac.uns.acs.nais.DynamicPricingService.model.relationship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import rs.ac.uns.acs.nais.DynamicPricingService.model.Purchase;
import rs.ac.uns.acs.nais.DynamicPricingService.model.enums.PurchaseMethod;

@RelationshipProperties
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MadePurchase {

    @RelationshipId
    private Long id;

    private PurchaseMethod purchaseMethod;

    @TargetNode
    private Purchase purchase;
}
