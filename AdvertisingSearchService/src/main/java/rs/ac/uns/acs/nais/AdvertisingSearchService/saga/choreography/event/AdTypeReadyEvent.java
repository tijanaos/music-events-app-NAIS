package rs.ac.uns.acs.nais.AdvertisingSearchService.saga.choreography.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdTypeReadyEvent {
    private String sagaId;
    private Long adTypeId;
    private boolean adTypeCreatedInSaga;
    private AdPayloadEvent ad;
}
