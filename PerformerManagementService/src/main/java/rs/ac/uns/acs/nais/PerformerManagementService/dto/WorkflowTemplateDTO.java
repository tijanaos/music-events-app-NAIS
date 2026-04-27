package rs.ac.uns.acs.nais.PerformerManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowTemplateDTO {
    private String name;
    private String description;
    private Boolean archived;
}
