package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceUsageResponse {

    private String id;
    private String resursId;
    private String nazivResursa;
    private String tipResursa;
    private Boolean prenosiv;
    private Integer dodeljenaKolicina;
    private String binaId;
    private String nazivBine;
    private String tipBine;
    private String terminId;
    private LocalDate datum;
    private Integer vremePocetka;
    private Integer vremeKraja;
    private Boolean pozajmljenoSaBine;
    private String nazivBinePozajmice;
    private String rezervacijaId;
}
