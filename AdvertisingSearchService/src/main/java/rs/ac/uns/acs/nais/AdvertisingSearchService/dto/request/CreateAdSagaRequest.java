package rs.ac.uns.acs.nais.AdvertisingSearchService.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAdSagaRequest {

    @Valid
    @NotNull
    private AdTypeRequest adType;

    @Valid
    @NotNull
    private SagaAdPayloadRequest ad;
}
