package rs.ac.uns.acs.nais.DynamicPricingService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.PurchaseRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.PurchaseResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.model.Purchase;

@Mapper(componentModel = "spring", uses = {TicketTypeMapper.class})
public interface PurchaseMapper {

    @Mapping(target = "promoCode", source = "promoCode.code")
    PurchaseResponse toResponse(Purchase purchase);

    @Mapping(target = "ticketType", ignore = true)
    @Mapping(target = "promoCode",  ignore = true)
    Purchase toEntity(PurchaseRequest request);
}
