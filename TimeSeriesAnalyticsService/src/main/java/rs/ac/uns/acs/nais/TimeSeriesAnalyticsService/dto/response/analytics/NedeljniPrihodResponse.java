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
public class NedeljniPrihodResponse {
    private String tipKarte;
    private String tierKupca;
    private Instant nedelja;
    private Double ukupanPrihod;
    private Double ukupnoKarata;
}
