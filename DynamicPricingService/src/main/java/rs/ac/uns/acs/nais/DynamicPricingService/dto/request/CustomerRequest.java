package rs.ac.uns.acs.nais.DynamicPricingService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.acs.nais.DynamicPricingService.model.enums.CustomerTier;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

    private String name;
    private String email;
    private CustomerTier tier;
}
