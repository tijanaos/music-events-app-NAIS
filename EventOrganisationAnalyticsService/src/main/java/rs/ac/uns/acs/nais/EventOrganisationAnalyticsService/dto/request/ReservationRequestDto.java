package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDto {

    @NotBlank
    private String requestStatus;

    @NotNull
    private LocalDate sentDate;

    private LocalDate updatedDate;

    private String note;

    @NotBlank
    private String stageId;

    @NotBlank
    private String stageName;

    @NotBlank
    private String stageType;

    @NotNull
    private Integer stageCapacity;

    @NotBlank
    private String performerId;

    @NotBlank
    private String performerFirstName;

    @NotBlank
    private String performerLastName;

    @NotBlank
    private String genre;

    @NotNull
    private Double popularity;

    @NotNull
    private LocalDate performanceDate;

    @NotNull
    private Integer startTime;

    @NotNull
    private Integer endTime;

    private List<RequestedResourceItemRequest> requestedResources;

    private Boolean hasTasks;

    private Integer taskCount;

    private String performanceDetails;
}
