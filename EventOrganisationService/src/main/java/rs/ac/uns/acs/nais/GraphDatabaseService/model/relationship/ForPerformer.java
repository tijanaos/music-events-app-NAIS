package rs.ac.uns.acs.nais.GraphDatabaseService.model.relationship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Performer;

@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForPerformer {

    @RelationshipId
    private Long relId;

    private String managerUsername;
    private Double agreedFee;

    @TargetNode
    private Performer performer;
}
