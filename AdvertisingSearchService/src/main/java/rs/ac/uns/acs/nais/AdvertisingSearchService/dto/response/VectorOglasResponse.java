package rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VectorOglasResponse {
    private Integer oglasId;
    private Integer adTypeId;
    private String naziv;
    private String opis;
    private String tipOglasa;
    private String contentUrl;
    private String status;
    private String kategorija;
    private String datumKreiranja;
    private String datumPoslednjeIzmene;
    private Integer kampanjaId;
}
