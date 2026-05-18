package rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdPhaseSearchQueryResponse {
    @Schema(description = "Ukupan broj faza koje zadovoljavaju uslove upita.")
    private long totalHits;

    @Schema(description = "Lista konkretnih faza koje su pronađene.")
    private List<AdPhaseResponse> results;

    @Schema(description = "Agregirani pregled pronađenih faza grupisan po odgovornoj ulozi.")
    private List<AggregationBucketResponse> groupedByResponsibleRole;
}
