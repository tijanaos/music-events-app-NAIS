package rs.ac.uns.acs.nais.GraphDatabaseService.dto.query;

public interface GenreReservationStats {
    String getGenre();
    Long getReservationCount();
    Double getAverageFee();
    Double getAvgPopularity();
}
