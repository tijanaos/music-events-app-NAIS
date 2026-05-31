package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Measurement(name = "kupovina")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KupovinaPoint {

    @Column(tag = true)
    private String festivalId;

    @Column(tag = true)
    private String nazivFestivala;

    @Column(tag = true)
    private String tipKarte;

    @Column(tag = true)
    private String tierKupca;

    @Column(tag = true)
    private String promoKoriscen;

    @Column
    private Double kolicina;

    @Column
    private Double ukupnaCena;

    @Column
    private Double cenaPoPKarti;

    @Column(timestamp = true)
    private Instant time;
}
