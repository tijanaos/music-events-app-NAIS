package rs.ac.uns.acs.nais.GraphDatabaseService.dto.query;

public interface PerformerBookingStats {
    String getPerformerId();
    String getPerformerName();
    String getGenre();
    Long getBookingCount();
    Double getAverageFee();
}
