package rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AggregationBucketResponse {
    @Schema(description = "Vrednost polja po kome su rezultati grupisani, npr. social_media, Pravni tim ili festival.")
    private String groupValue;

    @Schema(description = "Broj dokumenata koji pripadaju ovoj grupi nakon primene svih uslova iz upita.")
    private Long matchingDocumentsCount;

    @Schema(description = "Prosečna vrednost agregirane metrike za ovu grupu, npr. prosečno trajanje oglasa u danima ili prosečno trajanje faze u satima.")
    private Double averageCalculatedValue;
}
