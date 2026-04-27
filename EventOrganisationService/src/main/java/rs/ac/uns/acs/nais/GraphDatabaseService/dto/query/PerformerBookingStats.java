package rs.ac.uns.acs.nais.GraphDatabaseService.dto.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformerBookingStats {
    private String performerId;
    private String performerName;
    private String genre;
    private Long bookingCount;
    private Double averageFee;
}
