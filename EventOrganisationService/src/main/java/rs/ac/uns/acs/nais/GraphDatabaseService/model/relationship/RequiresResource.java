package rs.ac.uns.acs.nais.GraphDatabaseService.model.relationship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Resource;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ResourceRequestStatus;

@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequiresResource {

    @RelationshipId
    private Long relId;

    private Integer requestedQuantity;
    private ResourceRequestStatus status;
    private String rejectionReason;
    private Boolean existsInSystem;
    private String managerNote;
    private String borrowingSourceId;

    @TargetNode
    private Resource resource;
}
