package rs.ac.uns.acs.nais.DynamicPricingService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.acs.nais.DynamicPricingService.model.enums.PurchaseMethod;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MadePurchaseResponse {

    private PurchaseMethod purchaseMethod;
    private PurchaseResponse purchase;
}
