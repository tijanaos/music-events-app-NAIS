package rs.ac.uns.acs.nais.NegotiationAnalyticsService.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

// Belezimo za svaku ponudu prelazak u neko drugo stanje, cenu i lokaciju

@Measurement(name = "offer_lifecycle_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferLifecycleEvent {

    @Column(tag = true)
    private String offer_id;

    @Column(tag = true)
    private String event_type;

    @Column(tag = true)
    private String manager_id;

    @Column(tag = true)
    private String negotiation_id;

    @Column
    private double price;

    @Column(tag = true)
    private String location;

    @Column(timestamp = true)
    private Instant occurred_at;
}
