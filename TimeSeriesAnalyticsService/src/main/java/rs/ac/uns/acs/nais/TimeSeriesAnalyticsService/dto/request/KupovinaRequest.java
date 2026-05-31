package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.request;

import lombok.Data;

import java.time.Instant;

@Data
public class KupovinaRequest {
    private String festivalId;
    private String nazivFestivala;
    private String tipKarte;
    private String tierKupca;
    private boolean promoKoriscen;
    private Double kolicina;
    private Double ukupnaCena;
    private Instant time;
}
