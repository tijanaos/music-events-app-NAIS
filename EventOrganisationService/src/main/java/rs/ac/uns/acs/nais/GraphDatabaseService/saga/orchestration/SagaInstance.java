package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ReservationDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class SagaInstance {

    private final String sagaId;
    private final ReservationDTO reservationRequest;

    private SagaState state;
    private String reservationId;
    private String errorMessage;
    private final LocalDateTime createdAt;
    private final List<StateTransition> history = new ArrayList<>();

    public SagaInstance(String sagaId, ReservationDTO reservationRequest) {
        this.sagaId = sagaId;
        this.reservationRequest = reservationRequest;
        this.createdAt = LocalDateTime.now();
        setState(SagaState.STARTED);
    }

    public void setState(SagaState newState) {
        this.state = newState;
        this.history.add(new StateTransition(newState, LocalDateTime.now()));
    }

    public void failWith(SagaState failState, String reason) {
        this.errorMessage = reason;
        setState(failState);
    }

    public List<StateTransition> getHistory() {
        return Collections.unmodifiableList(history);
    }

    @Getter
    public static class StateTransition {
        private final SagaState state;
        private final LocalDateTime timestamp;

        public StateTransition(SagaState state, LocalDateTime timestamp) {
            this.state = state;
            this.timestamp = timestamp;
        }
    }
}