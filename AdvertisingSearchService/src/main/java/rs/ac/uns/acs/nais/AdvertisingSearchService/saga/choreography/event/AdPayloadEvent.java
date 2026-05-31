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
public class AdPayloadEvent {
    private Integer oglasId;
    private Long adTypeId;
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
