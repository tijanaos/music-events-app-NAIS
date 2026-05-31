package rs.ac.uns.acs.nais.AdvertisingSearchService.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SagaAdPayloadRequest {

    @NotNull
    private Integer oglasId;

    @NotBlank
    private String naziv;

    @NotBlank
    private String opis;

    @NotBlank
    private String tipOglasa;

    private String contentUrl;

    @NotBlank
    private String status;

    @NotBlank
    private String kategorija;

    @NotBlank
    private String datumKreiranja;

    @NotBlank
    private String datumPoslednjeIzmene;

    @NotNull
    private Integer kampanjaId;
}
