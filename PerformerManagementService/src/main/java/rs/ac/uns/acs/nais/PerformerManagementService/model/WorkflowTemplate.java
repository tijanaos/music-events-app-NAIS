package rs.ac.uns.acs.nais.PerformerManagementService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import rs.ac.uns.acs.nais.PerformerManagementService.model.relationship.ContainsState;

import java.util.List;

@Node("WorkflowTemplate")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowTemplate {

    @Id
    private String id;

    private String name;
    private String description;
    private Boolean archived;

    @Relationship(type = "CONTAINS_STATE", direction = Relationship.Direction.OUTGOING)
    private List<ContainsState> states;
}
