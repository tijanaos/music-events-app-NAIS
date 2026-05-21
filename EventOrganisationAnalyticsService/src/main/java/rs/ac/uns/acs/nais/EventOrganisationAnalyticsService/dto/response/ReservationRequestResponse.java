package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ZahtevaniResursItem;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestResponse {

    private String id;
    private String statusZahteva;
    private LocalDate datumSlanja;
    private LocalDate datumAzuriranja;
    private String napomena;
    private String binaId;
    private String nazivBine;
    private String tipBine;
    private Integer kapacitetBine;
    private String izvodjacId;
    private String imeIzvodjaca;
    private String prezimeIzvodjaca;
    private String zanr;
    private Double popularnost;
    private LocalDate datumNastupa;
    private Integer vremePocetka;
    private Integer vremeKraja;
    private List<ZahtevaniResursItem> zahtevanihResursa;
    private Boolean imaTaskove;
    private Integer brojTaskova;
    private String detaljiNastupa;
}
