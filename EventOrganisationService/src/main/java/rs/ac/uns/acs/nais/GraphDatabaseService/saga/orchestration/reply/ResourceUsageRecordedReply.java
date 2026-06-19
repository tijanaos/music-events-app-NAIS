package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.reply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Reply koji Analytics servis (Elasticsearch) salje nazad orkestratoru nakon
 * obrade RecordResourceUsageCommand -- drugog koraka sage.
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