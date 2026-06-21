package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.reply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command.ResourceUsageEntry;

import java.util.List;

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