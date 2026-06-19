package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Komanda koju SagaOrchestrator salje Analytics servisu (Elasticsearch) da
 * upise zapise o iskoriscenosti resursa za novokreiranu rezervaciju.
 *
 * Lista resourceUsageEntries je vec u potpunosti popunjena od strane Neo4j
 * CommandListener-a (nazivi bine/resursa, termin...), tako da Analytics servis
 * samo direktno mapira u ResourceUsageDocument i cuva u indeks "resource-usage".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordResourceUsageCommand {

    private String sagaId;
    private String reservationId;
    private List<ResourceUsageEntry> resourceUsageEntries;
}