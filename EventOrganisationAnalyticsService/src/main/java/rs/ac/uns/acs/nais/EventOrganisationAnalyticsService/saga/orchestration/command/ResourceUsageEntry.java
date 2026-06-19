package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.saga.orchestration.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Nosi sve podatke o iskoriscenosti jednog resursa, vec obogacene nazivima i
 * tipovima iz Neo4j grafa (bina, termin, resurs). Strukturom identican
 * ResourceUsageEntry u EventOrganisationService -- obe klase predstavljaju isti
 * "ugovor" o formatu poruke koji se razmenjuje preko RabbitMQ-a.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceUsageEntry {

    private String resourceId;
    private String resourceName;
    private String resourceType;
    private Boolean portable;
    private Integer allocatedQuantity;

    private String stageId;
    private String stageName;
    private String stageType;

    private String timeSlotId;
    private LocalDate date;
    private Integer startTime;
    private Integer endTime;

    private Boolean borrowedFromStage;
    private String borrowingStageName;
}