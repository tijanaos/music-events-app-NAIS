package rs.ac.uns.acs.nais.GraphDatabaseService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ReservationStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {

    private ReservationStatus status;
    private String note;
    private String performanceDetails;
    private String createdBy;

    private String stageId;
    private Boolean stageConfirmed;

    private String timeSlotId;
    private LocalDate slotReservationDate;
    private Boolean systemSuggestion;

    private String performerId;
    private String managerUsername;
    private Double agreedFee;
}
