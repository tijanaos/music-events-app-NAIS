package rs.ac.uns.acs.nais.DynamicPricingService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeRequest {

    private String code;
    private Integer discountPercent;
    private String validFrom;
    private String validTo;
    private Integer maxUses;
    private Integer currentUses;
    private List<ValidForRequest> validForTickets;
}
