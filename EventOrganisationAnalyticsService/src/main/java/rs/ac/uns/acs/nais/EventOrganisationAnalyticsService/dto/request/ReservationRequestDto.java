package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDto {

    @NotBlank
    private String statusZahteva;

    @NotNull
    private LocalDate datumSlanja;

    private LocalDate datumAzuriranja;

    private String napomena;

    @NotBlank
    private String binaId;

    @NotBlank
    private String nazivBine;

    @NotBlank
    private String tipBine;

    @NotNull
    private Integer kapacitetBine;

    @NotBlank
    private String izvodjacId;

    @NotBlank
    private String imeIzvodjaca;

    @NotBlank
    private String prezimeIzvodjaca;

    @NotBlank
    private String zanr;

    @NotNull
    private Double popularnost;

    @NotNull
    private LocalDate datumNastupa;

    @NotNull
    private Integer vremePocetka;

    @NotNull
    private Integer vremeKraja;

    private List<ZahtevaniResursItemRequest> zahtevanihResursa;

    private Boolean imaTaskove;

    private Integer brojTaskova;

    private String detaljiNastupa;
}
