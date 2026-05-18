package rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdPhaseResponse {
    private Long id;
    private Long adTypeId;
    private String phaseName;
    private String description;
    private Integer phaseOrder;
    private String responsibleRole;
    private Boolean requiresEmailNotification;
    private Boolean isFinalPhase;
    private Boolean isActive;
    private Integer expectedDurationHours;
    private LocalDate createdAt;
}
