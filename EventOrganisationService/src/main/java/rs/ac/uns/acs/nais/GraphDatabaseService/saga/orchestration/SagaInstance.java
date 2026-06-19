package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ReservationDTO;

import java.time.LocalDateTime;

/**
 * Predstavlja jednu aktivnu (ili zavrsenu) instancu sage kreiranja rezervacije.
 * Cuva se u memorijskom registru unutar SagaOrchestrator-a, kljucano po sagaId.
 *
 * Za produkcionu upotrebu, preporucljivo je zameniti in-memory registar
 * trajnim skladistem (npr. Redis ili relaciona baza), kako bi stanje sage
 * preživelo restart servisa.
 */
@Getter
@Setter
public class SagaInstance {

    private final String sagaId;
    private final ReservationDTO reservationRequest;

    private SagaState state;
    private String reservationId;
    private final LocalDateTime createdAt;

    public SagaInstance(String sagaId, ReservationDTO reservationRequest) {
        this.sagaId = sagaId;
        this.reservationRequest = reservationRequest;
        this.state = SagaState.STARTED;
        this.createdAt = LocalDateTime.now();
    }
}