package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.saga.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NegotiationConcludedEvent {
    private String sagaId;
    private String negotiationId;
    private String performerId;
    private String performerName;
    private Double agreedFee;
}
