package rs.ac.uns.acs.nais.GraphDatabaseService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Performer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Performer {

    @Id
    private String id;

    private String name;
    private String genre;
    private Double popularity;
    private Integer averagePerformanceDuration;
    private String countryOfOrigin;
}
