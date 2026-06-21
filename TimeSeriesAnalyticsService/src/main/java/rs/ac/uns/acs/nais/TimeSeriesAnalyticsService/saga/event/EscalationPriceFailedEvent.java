package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.saga.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EscalationPriceFailedEvent {
    private String sagaId;
    private String negotiationId;
    private String offerId;
    private Double staraCena;
    private String reason;
}