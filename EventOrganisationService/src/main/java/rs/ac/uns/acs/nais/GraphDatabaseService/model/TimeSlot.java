package rs.ac.uns.acs.nais.GraphDatabaseService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.SlotType;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.TimeSlotStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Node("TimeSlot")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlot {

    @Id
    private String id;

    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMin;
    private TimeSlotStatus status;
    private SlotType slotType;
}
