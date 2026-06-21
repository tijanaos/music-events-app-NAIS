package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration;

public enum SagaState {
    STARTED,
    RESERVATION_CREATED,
    USAGE_RECORDED,
    COMPLETED,
    COMPENSATING,
    COMPENSATED,
    FAILED
}