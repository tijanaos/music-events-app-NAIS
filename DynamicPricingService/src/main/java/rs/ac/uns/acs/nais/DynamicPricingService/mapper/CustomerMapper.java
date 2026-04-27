package rs.ac.uns.acs.nais.DynamicPricingService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.CustomerRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.CustomerResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.MadePurchaseResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.model.Customer;
import rs.ac.uns.acs.nais.DynamicPricingService.model.relationship.MadePurchase;

@Mapper(componentModel = "spring", uses = {PurchaseMapper.class})
public interface CustomerMapper {

    CustomerResponse toResponse(Customer customer);

    @Mapping(target = "purchase", source = "purchase")
    MadePurchaseResponse toMadePurchaseResponse(MadePurchase madePurchase);

    @Mapping(target = "purchases", ignore = true)
    Customer toEntity(CustomerRequest request);
}
