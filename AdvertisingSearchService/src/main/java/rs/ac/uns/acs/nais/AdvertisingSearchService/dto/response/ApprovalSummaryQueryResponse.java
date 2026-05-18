package rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ApprovalSummaryQueryResponse {
    @Schema(description = "Ukupan broj vrsta oglasa koje zadovoljavaju uslove upita.")
    private long totalHits;

    @Schema(description = "Lista konkretnih vrsta oglasa koje su pronađene.")
    private List<AdTypeResponse> results;

    @Schema(description = "Agregirani pregled pronađenih oglasa grupisan po kategoriji.")
    private List<AggregationBucketResponse> groupedByCategory;
}
