package rs.ac.uns.acs.nais.PerformerManagementService.saga.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
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