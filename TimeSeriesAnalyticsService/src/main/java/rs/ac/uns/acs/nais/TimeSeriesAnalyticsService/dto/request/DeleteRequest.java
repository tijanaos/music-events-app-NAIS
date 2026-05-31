package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.request;

import lombok.Data;

import java.time.Instant;

@Data
public class DeleteRequest {
    private Instant start;
    private Instant stop;
    private String festivalId;
    private String tipKarte;
}
