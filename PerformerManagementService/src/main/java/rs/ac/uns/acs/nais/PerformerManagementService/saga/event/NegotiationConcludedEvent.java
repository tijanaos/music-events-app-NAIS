package rs.ac.uns.acs.nais.PerformerManagementService.saga.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NegotiationConcludedEvent {
    private String sagaId;
    private String negotiationId;
    private String performerId;
    private String performerName;
    private Double agreedFee;
}
