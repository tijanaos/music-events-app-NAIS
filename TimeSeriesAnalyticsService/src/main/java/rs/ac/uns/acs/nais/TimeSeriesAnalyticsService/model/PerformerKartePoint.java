package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Measurement(name = "performer_karte")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformerKartePoint {

    @Column(tag = true)
    private String negotiationId;

    @Column(tag = true)
    private String performerId;

    @Column(tag = true)
    private String performerName;

    @Column
    private Double brojKarata;

    @Column
    private Double agreedFee;

    @Column(timestamp = true)
    private Instant time;
}
