package rs.ac.uns.acs.nais.PerformerManagementService.model.relationship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import rs.ac.uns.acs.nais.PerformerManagementService.model.Offer;

import java.time.LocalDateTime;

@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatedFrom {

    @RelationshipId
    private Long relId;

    private LocalDateTime startedAt;

    @TargetNode
    private Offer offer;
}
