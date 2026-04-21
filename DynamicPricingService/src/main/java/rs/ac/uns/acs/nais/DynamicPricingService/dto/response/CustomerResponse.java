package rs.ac.uns.acs.nais.DynamicPricingService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.acs.nais.DynamicPricingService.model.enums.CustomerTier;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private String customerId;
    private String name;
    private String email;
    private CustomerTier tier;
    private List<MadePurchaseResponse> purchases;
}
