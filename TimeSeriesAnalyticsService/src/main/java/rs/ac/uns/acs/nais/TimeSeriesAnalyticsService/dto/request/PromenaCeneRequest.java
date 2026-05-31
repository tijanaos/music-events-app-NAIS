package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.request;

import lombok.Data;

import java.time.Instant;

@Data
public class PromenaCeneRequest {
    private String festivalId;
    private String nazivFestivala;
    private String tipKarte;
    private String razlog;
    private Double staraCena;
    private Double novaCena;
    private Instant time;
}
