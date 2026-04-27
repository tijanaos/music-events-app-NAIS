package rs.ac.uns.acs.nais.GraphDatabaseService.dto.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageResourceSummary {
    private String stageId;
    private String stageName;
    private String stageType;
    private Long totalResources;
    private Long totalAvailableQuantity;
}
