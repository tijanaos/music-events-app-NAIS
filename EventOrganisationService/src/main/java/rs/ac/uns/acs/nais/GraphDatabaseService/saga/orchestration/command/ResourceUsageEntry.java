package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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