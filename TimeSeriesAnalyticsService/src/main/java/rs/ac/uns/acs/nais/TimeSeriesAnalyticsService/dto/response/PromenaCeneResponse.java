package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromenaCeneResponse {
    private String festivalId;
    private String nazivFestivala;
    private String tipKarte;
    private String razlog;
    private Double staraCena;
    private Double novaCena;
    private Double deltaCene;
    private Double procenatPromene;
    private Instant time;
}
