package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Measurement(name = "promena_cene")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromenaCenePoint {

    @Column(tag = true)
    private String festivalId;

    @Column(tag = true)
    private String nazivFestivala;

    @Column(tag = true)
    private String tipKarte;

    @Column(tag = true)
    private String razlog;

    @Column
    private Double staraCena;

    @Column
    private Double novaCena;

    @Column
    private Double deltaCene;

    @Column
    private Double procenatPromene;

    @Column(timestamp = true)
    private Instant time;
}
