package rs.ac.uns.acs.nais.DynamicPricingService.model.relationship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import rs.ac.uns.acs.nais.DynamicPricingService.model.TicketType;

@RelationshipProperties
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidFor {

    @RelationshipId
    private Long id;

    private Integer minQuantity;

    @TargetNode
    private TicketType ticketType;
}
