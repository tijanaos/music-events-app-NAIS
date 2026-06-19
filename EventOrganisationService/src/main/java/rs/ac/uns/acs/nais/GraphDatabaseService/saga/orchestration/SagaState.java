package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration;

/**
 * Moguca stanja kroz koja prolazi orkestrisana saga kreiranja rezervacije.
 *
 *   STARTED
 *     -> RESERVATION_CREATED   (korak 1 uspeo: Neo4j kreirao Reservation)
 *     -> USAGE_RECORDED        (korak 2 uspeo: ES upisao ResourceUsageDocument-e)
 *     -> COMPLETED             (saga uspesno zavrsena)
 *
 *   Ako korak 2 ne uspe:
 *     -> COMPENSATING -> COMPENSATED   (Neo4j obrisao rezervaciju)
 *                     -> FAILED        (kompenzacija nije uspela -- nekonzistentno stanje)
 *
 *   Ako korak 1 ne uspe ili poruka ne moze da se posalje:
 *     -> FAILED
 */
public enum SagaState {
    STARTED,
    RESERVATION_CREATED,
    USAGE_RECORDED,
    COMPLETED,
    COMPENSATING,
    COMPENSATED,
    FAILED
}