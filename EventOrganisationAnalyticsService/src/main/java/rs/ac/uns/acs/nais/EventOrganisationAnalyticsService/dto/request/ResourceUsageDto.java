package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ResourceUsageDto {

    @NotBlank
    private String resourceId;

    @NotBlank
    private String resourceName;

    @NotBlank
    private String resourceType;

    @NotNull
    private Boolean portable;

    @NotNull
    @Min(1)
    private Integer allocatedQuantity;

    @NotBlank
    private String stageId;

    @NotBlank
    private String stageName;

    @NotBlank
    private String stageType;

    @NotBlank
    private String timeSlotId;

    @NotNull
    private LocalDate date;

    @NotNull
    private Integer startTime;

    @NotNull
    private Integer endTime;

    @NotNull
    private Boolean borrowedFromStage;

    private String borrowingStageName;

    @NotBlank
    private String reservationId;
}
