package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregationBucketResponse {

    private String key;
    private Long count;
    private Double numericValue;
}
