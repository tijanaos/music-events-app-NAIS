package rs.ac.uns.acs.nais.GraphDatabaseService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ResourceType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceDTO {

    private String name;
    private ResourceType type;
    private Integer quantity;
    private Boolean portable;
    private String description;
}
