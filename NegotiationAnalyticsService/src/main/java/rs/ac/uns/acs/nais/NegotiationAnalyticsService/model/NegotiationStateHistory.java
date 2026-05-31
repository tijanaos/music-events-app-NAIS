package rs.ac.uns.acs.nais.NegotiationAnalyticsService.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

// Belezimo za pregovor svaki prelazak u neko drugo stanje,
// koliko je dugo vremena proveo u njemu i
// koji je izvodjac u pregovoru

@Measurement(name = "negotiation_state_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NegotiationStateHistory {

    @Column(tag = true)
    private String negotiation_id;

    @Column(tag = true)
    private String state_name;

    @Column(tag = true)
    private String template_name;

    @Column(tag = true)
    private String manager_id;

    @Column(tag = true)
    private String negotiation_status;

    @Column
    private double duration_seconds;

    @Column
    private String performer_name;

    @Column(timestamp = true)
    private Instant entered_at;
}
