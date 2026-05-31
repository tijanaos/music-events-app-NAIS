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
public class KupovinaResponse {
    private String festivalId;
    private String nazivFestivala;
    private String tipKarte;
    private String tierKupca;
    private String promoKoriscen;
    private Double kolicina;
    private Double ukupnaCena;
    private Double cenaPoPKarti;
    private Instant time;
}
