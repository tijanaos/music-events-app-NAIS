package rs.ac.uns.acs.nais.PerformerManagementService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import rs.ac.uns.acs.nais.PerformerManagementService.model.relationship.CreatedFrom;
import rs.ac.uns.acs.nais.PerformerManagementService.model.relationship.InState;
import rs.ac.uns.acs.nais.PerformerManagementService.model.relationship.PartOf;

import java.time.LocalDateTime;
import java.util.List;

@Node("Negotiation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Negotiation {

    @Id
    private String id;

    private String createdBy;
    private String failReason;
    private String failedAtStateName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime concludedAt;

    @Relationship(type = "CREATED_FROM", direction = Relationship.Direction.OUTGOING)
    private CreatedFrom offer;

    @Relationship(type = "IN_STATE", direction = Relationship.Direction.OUTGOING)
    private InState currentState;

    @Relationship(type = "PART_OF", direction = Relationship.Direction.INCOMING)
    private List<PartOf> performers;
}
