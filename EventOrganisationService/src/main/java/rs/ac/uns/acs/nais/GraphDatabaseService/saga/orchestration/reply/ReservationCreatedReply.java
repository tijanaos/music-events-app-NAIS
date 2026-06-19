package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.reply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command.ResourceUsageEntry;

import java.util.List;

/**
 * Reply koji Neo4j CommandListener salje nazad orkestratoru nakon obrade
 * CreateReservationCommand.
 *
 * Na uspehu nosi id novokreirane rezervacije i listu obogacenih zapisa o
 * trazenim resursima (ResourceUsageEntry) -- orkestrator ih prosledjuje u
 * RecordResourceUsageCommand ka Analytics servisu, bez potrebe za dodatnim
 * upitom ka Neo4j bazi.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCreatedReply {

    private String sagaId;
    private boolean success;
    private String errorMessage;

    private String reservationId;
    private List<ResourceUsageEntry> resourceUsageEntries;
}