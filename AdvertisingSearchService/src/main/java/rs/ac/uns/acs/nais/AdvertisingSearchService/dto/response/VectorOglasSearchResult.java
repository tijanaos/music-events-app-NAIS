package rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VectorOglasSearchResult extends VectorOglasResponse {
    private Integer id;
    private Double score;
    private Double fusedScore;
}
