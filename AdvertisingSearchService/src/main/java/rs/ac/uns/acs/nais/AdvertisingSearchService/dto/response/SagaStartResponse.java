package rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SagaStartResponse {
    private String sagaId;
    private Long adTypeId;
    private boolean adTypeCreated;
    private String status;
    private String message;
}
