package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceUtilizationReportResponse {

    private Long totalHits;
    private List<AggregationBucketResponse> resourcesByFrequency;
    private List<AggregationBucketResponse> timeSlotsResourceCount;
    private Long reservationsWithTasks;
}
