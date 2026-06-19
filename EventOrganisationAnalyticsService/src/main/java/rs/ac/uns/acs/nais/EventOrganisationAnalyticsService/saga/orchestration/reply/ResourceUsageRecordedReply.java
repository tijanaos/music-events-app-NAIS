package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.saga.orchestration.reply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Reply koji ovaj servis salje orkestratoru u EventOrganisationService nakon
 * obrade RecordResourceUsageCommand.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceUsageRecordedReply {

    private String sagaId;
    private boolean success;
    private String errorMessage;

    private Integer recordedCount;
}