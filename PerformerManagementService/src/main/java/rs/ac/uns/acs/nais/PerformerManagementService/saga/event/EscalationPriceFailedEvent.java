package rs.ac.uns.acs.nais.PerformerManagementService.saga.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscalationPriceFailedEvent {
    private String sagaId;
    private String negotiationId;
    private String offerId;
    private Double staraCena;
    private String reason;
}