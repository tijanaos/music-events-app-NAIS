package rs.ac.uns.acs.nais.DynamicPricingService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequest {

    private String purchaseId;
    private String date;
    private Integer quantity;
    private Double unitPrice;
    private Double tierDiscountApplied;
    private Double promoDiscountApplied;
    private Double finalPrice;
    private String ticketTypeId;
    private String promoCode;
}
