package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ResourceUsageDto {

    @NotBlank
    private String resursId;

    @NotBlank
    private String nazivResursa;

    @NotBlank
    private String tipResursa;

    @NotNull
    private Boolean prenosiv;

    @NotNull
    @Min(1)
    private Integer dodeljenaKolicina;

    @NotBlank
    private String binaId;

    @NotBlank
    private String nazivBine;

    @NotBlank
    private String tipBine;

    @NotBlank
    private String terminId;

    @NotNull
    private LocalDate datum;

    @NotNull
    private Integer vremePocetka;

    @NotNull
    private Integer vremeKraja;

    @NotNull
    private Boolean pozajmljenoSaBine;

    private String nazivBinePozajmice;

    @NotBlank
    private String rezervacijaId;
}
