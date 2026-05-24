package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.RequestedResourceItem;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestResponse {

    private String id;
    private String requestStatus;
    private LocalDate sentDate;
    private LocalDate updatedDate;
    private String note;
    private String stageId;
    private String stageName;
    private String stageType;
    private Integer stageCapacity;
    private String performerId;
    private String performerFirstName;
    private String performerLastName;
    private String genre;
    private Double popularity;
    private LocalDate performanceDate;
    private Integer startTime;
    private Integer endTime;
    private List<RequestedResourceItem> requestedResources;
    private Boolean hasTasks;
    private Integer taskCount;
    private String performanceDetails;
}
