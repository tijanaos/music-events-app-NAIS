package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.reply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Reply koji Neo4j CommandListener salje nazad orkestratoru nakon obrade
 * DeleteReservationCommand (kompenzaciona faza sage).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDeletedReply {

    private String sagaId;
    private boolean success;
    private String errorMessage;
}