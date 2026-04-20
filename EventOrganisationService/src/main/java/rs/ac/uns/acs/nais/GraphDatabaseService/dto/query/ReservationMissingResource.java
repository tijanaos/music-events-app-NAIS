package rs.ac.uns.acs.nais.GraphDatabaseService.dto.query;

public interface ReservationMissingResource {
    String getReservationId();
    String getReservationStatus();
    String getCreatedBy();
    String getResourceName();
    String getResourceType();
    Long getRequestedQuantity();
}
