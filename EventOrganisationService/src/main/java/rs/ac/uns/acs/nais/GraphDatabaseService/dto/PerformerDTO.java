package rs.ac.uns.acs.nais.GraphDatabaseService.dto;

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
    private Double popularity;
    private Integer averagePerformanceDuration;
    private String countryOfOrigin;
}
