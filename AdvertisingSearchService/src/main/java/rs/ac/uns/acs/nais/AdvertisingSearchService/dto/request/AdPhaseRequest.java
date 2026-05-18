package rs.ac.uns.acs.nais.AdvertisingSearchService.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdPhaseRequest {

    private Long id;

    @NotNull
    private Long adTypeId;

    @NotBlank
    private String phaseName;

    @NotBlank
    private String description;

    @NotNull
    private Integer phaseOrder;

    @NotBlank
    private String responsibleRole;

    @NotNull
    private Boolean requiresEmailNotification;

    @NotNull
    private Boolean isFinalPhase;

    @NotNull
    private Boolean isActive;

    @NotNull
    private Integer expectedDurationHours;

    private LocalDate createdAt;
}
