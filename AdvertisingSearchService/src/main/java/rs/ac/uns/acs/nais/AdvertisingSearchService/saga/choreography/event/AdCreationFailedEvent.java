package rs.ac.uns.acs.nais.AdvertisingSearchService.saga.choreography.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdCreationFailedEvent {
    private String sagaId;
    private Long adTypeId;
    private Integer oglasId;
    private boolean adTypeCreatedInSaga;
    private String reason;
}
