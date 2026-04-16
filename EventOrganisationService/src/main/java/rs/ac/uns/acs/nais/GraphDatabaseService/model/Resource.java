package rs.ac.uns.acs.nais.GraphDatabaseService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ResourceType;

@Node("Resource")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource {

    @Id
    @GeneratedValue(GeneratedValue.UUIDStringGenerator.class)
    private String id;

    private String name;
    private ResourceType type;
    private Integer quantity;
    private Boolean portable;
    private String description;
}
