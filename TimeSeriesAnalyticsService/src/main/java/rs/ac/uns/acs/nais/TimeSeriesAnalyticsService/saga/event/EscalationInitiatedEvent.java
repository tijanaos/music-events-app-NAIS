package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.saga.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EscalationInitiatedEvent {
    private String sagaId;
    private String negotiationId;
    private String offerId;
    private String performerName;
    private Double staraCena;
    private Double novaCena;
}