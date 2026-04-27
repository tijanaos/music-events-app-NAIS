package rs.ac.uns.acs.nais.PerformerManagementService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("State")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class State {

    @Id
    private String id;

    private String name;
    private String description;
    private Boolean isInitial;
    private Boolean isFinal;
    private Integer maxDurationDays;
}
