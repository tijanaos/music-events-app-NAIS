package rs.ac.uns.acs.nais.GraphDatabaseService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ResourceRequestStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequiresResourceDTO {

    private String resourceId;
    private Integer requestedQuantity;
    private ResourceRequestStatus status;
    private Boolean existsInSystem;
    private String managerNote;
    private String rejectionReason;
    private String borrowingSourceId;
}
