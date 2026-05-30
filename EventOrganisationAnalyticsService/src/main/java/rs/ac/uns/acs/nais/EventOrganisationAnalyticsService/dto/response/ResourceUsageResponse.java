package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceUsageResponse {

    private String id;
    private String resourceId;
    private String resourceName;
    private String resourceType;
    private Boolean portable;
    private Integer allocatedQuantity;
    private String stageId;
    private String stageName;
    private String stageType;
    private String timeSlotId;
    private LocalDate date;
    private Integer startTime;
    private Integer endTime;
    private Boolean borrowedFromStage;
    private String borrowingStageName;
    private String reservationId;
}
