package rs.ac.uns.acs.nais.GraphDatabaseService.model.relationship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Stage;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.SharingType;

import java.time.LocalDate;

@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharesResource {

    @RelationshipId
    private Long relId;

    private String resourceId;
    private SharingType sharingType;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    @TargetNode
    private Stage stage;
}
