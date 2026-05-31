package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeakSatiResponse {
    private String tierKupca;
    private String tipKarte;
    private Long brojTransakcija;
    private Double prosecnaVrednostPorudzbine;
    private Double ukupanPrihod;
}
