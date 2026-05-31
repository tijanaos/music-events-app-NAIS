package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RastCenaResponse {
    private String tipKarte;
    private String festivalId;
    private String nazivFestivala;
    private Instant mesec;
    private Double ukupnaDeltaCene;
    private Double prosecniProcenatPromene;
    private Long brojPromena;
    private String smer;
}
