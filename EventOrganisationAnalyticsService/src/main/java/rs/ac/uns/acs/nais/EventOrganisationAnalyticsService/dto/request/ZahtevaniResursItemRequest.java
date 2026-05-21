package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ZahtevaniResursItemRequest {

    @NotBlank
    private String nazivResursa;

    @NotBlank
    private String tipResursa;

    @NotNull
    @Min(1)
    private Integer zahtevanrKolicina;

    @NotNull
    private Boolean postojiUSistemu;

    private String statusResursa;

    private String razlogOdbijanja;
}
