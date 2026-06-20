package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordResourceUsageCommand {

    private String sagaId;
    private String reservationId;
    private List<ResourceUsageEntry> resourceUsageEntries;
}