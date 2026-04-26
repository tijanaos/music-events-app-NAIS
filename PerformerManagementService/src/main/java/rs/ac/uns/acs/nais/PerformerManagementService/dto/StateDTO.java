package rs.ac.uns.acs.nais.PerformerManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StateDTO {
    private String name;
    private String description;
    private Boolean isInitial;
    private Boolean isFinal;
    private Integer maxDurationDays;
}
