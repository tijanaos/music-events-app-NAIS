package rs.ac.uns.acs.nais.PerformerManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformerDTO {
    private String name;
    private String genre;
    private String countryOfOrigin;
    private Integer memberCount;
    private Boolean archived;
}
