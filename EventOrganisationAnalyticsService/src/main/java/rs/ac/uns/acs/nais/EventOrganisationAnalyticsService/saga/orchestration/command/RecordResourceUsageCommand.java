package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.saga.orchestration.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Komanda koju SagaOrchestrator (EventOrganisationService) salje ovom servisu
 * da upise zapise o iskoriscenosti resursa u Elasticsearch -- drugi korak sage
 * kreiranja rezervacije.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordResourceUsageCommand {

    private String sagaId;
    private String reservationId;
    private List<ResourceUsageEntry> resourceUsageEntries;
}