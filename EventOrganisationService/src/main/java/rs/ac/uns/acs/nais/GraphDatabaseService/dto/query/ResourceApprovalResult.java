package rs.ac.uns.acs.nais.GraphDatabaseService.dto.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceApprovalResult {
    private String reservationId;
    private String resourceName;
    private String updatedStatus;
}
