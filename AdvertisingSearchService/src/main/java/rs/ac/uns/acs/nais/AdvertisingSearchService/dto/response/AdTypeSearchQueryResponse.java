package rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdTypeSearchQueryResponse {
    @Schema(description = "Ukupan broj pogodaka koji zadovoljavaju uslove upita.")
    private long totalHits;

    @Schema(description = "Lista konkretnih vrsta oglasa koje su pronađene.")
    private List<AdTypeResponse> results;

    @Schema(description = "Agregirani pregled pronađenih oglasa grupisan po ciljnom kanalu oglašavanja.")
    private List<AggregationBucketResponse> groupedByTargetChannel;
}
