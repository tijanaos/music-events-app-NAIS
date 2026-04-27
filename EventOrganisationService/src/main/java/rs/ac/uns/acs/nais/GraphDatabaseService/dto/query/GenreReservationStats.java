package rs.ac.uns.acs.nais.GraphDatabaseService.dto.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreReservationStats {
    private String genre;
    private Long reservationCount;
    private Double averageFee;
    private Double avgPopularity;
}
