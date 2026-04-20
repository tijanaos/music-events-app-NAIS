package rs.ac.uns.acs.nais.GraphDatabaseService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.SlotType;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.TimeSlotStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotDTO {

    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMin;
    private TimeSlotStatus status;
    private SlotType slotType;
}
