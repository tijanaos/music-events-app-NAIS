package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Nosi sve podatke o iskoriscenosti jednog resursa u okviru rezervacije, vec
 * obogacene nazivima/tipovima iz Neo4j grafa (bina, termin, resurs), kako bi
 * Analytics servis mogao direktno da formira ResourceUsageDocument bez dodatnih
 * poziva nazad ka Neo4j servisu.
 *
 * Popunjava ga CommandListener u EventOrganisationService nakon sto uspesno
 * kreira Reservation cvor i poveze ga sa trazenim resursima.
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