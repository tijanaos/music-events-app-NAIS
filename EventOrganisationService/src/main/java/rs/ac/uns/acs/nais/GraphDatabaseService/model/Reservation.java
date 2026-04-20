package rs.ac.uns.acs.nais.GraphDatabaseService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ReservationStatus;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.relationship.ForPerformer;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.relationship.OccupiesSlot;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.relationship.OnStage;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.relationship.RequiresResource;

import java.time.LocalDateTime;
import java.util.List;

@Node("Reservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    private String id;

    private ReservationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String note;
    private String performanceDetails;
    private String createdBy;

    @Relationship(type = "ON_STAGE", direction = Relationship.Direction.OUTGOING)
    private OnStage stage;

    @Relationship(type = "OCCUPIES_SLOT", direction = Relationship.Direction.OUTGOING)
    private OccupiesSlot timeSlot;

    @Relationship(type = "FOR_PERFORMER", direction = Relationship.Direction.OUTGOING)
    private ForPerformer performer;

    @Relationship(type = "REQUIRES_RESOURCE", direction = Relationship.Direction.OUTGOING)
    private List<RequiresResource> resources;
}
