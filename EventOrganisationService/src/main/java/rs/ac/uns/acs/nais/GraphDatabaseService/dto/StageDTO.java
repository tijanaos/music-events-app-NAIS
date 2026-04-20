package rs.ac.uns.acs.nais.GraphDatabaseService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.StageType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageDTO {

    private String name;
    private Integer capacity;
    private StageType type;
    private String location;
    private Boolean active;
}
