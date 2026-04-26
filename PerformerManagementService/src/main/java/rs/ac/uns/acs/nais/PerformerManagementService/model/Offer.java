package rs.ac.uns.acs.nais.PerformerManagementService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import rs.ac.uns.acs.nais.PerformerManagementService.model.enums.OfferStatus;
import rs.ac.uns.acs.nais.PerformerManagementService.model.relationship.BasedOn;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Node("Offer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Offer {

    @Id
    private String id;

    private Double price;
    private LocalDate eventDate;
    private String location;
    private Integer duration;
    private String additionalBenefits;
    private OfferStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;

    @Relationship(type = "BASED_ON", direction = Relationship.Direction.OUTGOING)
    private BasedOn workflowTemplate;
}
