package rs.ac.uns.acs.nais.GraphDatabaseService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.StageType;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.relationship.HasResource;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.relationship.SharesResource;

import java.util.List;

@Node("Stage")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stage {

    @Id
    private String id;

    private String name;
    private Integer capacity;
    private StageType type;
    private String location;
    private Boolean active;

    @Relationship(type = "HAS_RESOURCE", direction = Relationship.Direction.OUTGOING)
    private List<HasResource> resources;

    @Relationship(type = "SHARES_RESOURCE", direction = Relationship.Direction.OUTGOING)
    private List<SharesResource> sharedResources;
}
