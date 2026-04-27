package rs.ac.uns.acs.nais.DynamicPricingService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResponse {

    private String purchaseId;
    private String date;
    private Integer quantity;
    private Double unitPrice;
    private Double tierDiscountApplied;
    private Double promoDiscountApplied;
    private Double finalPrice;
    private TicketTypeResponse ticketType;
    private String promoCode;
}
