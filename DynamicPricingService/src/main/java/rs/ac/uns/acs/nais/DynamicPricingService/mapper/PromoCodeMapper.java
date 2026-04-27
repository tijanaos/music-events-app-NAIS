package rs.ac.uns.acs.nais.DynamicPricingService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.PromoCodeRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.PromoCodeResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.ValidForResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.model.PromoCode;
import rs.ac.uns.acs.nais.DynamicPricingService.model.relationship.ValidFor;

@Mapper(componentModel = "spring")
public interface PromoCodeMapper {

    @Mapping(target = "ticketTypeId",   source = "ticketType.ticketTypeId")
    @Mapping(target = "ticketTypeName", source = "ticketType.name")
    ValidForResponse toValidForResponse(ValidFor validFor);

    PromoCodeResponse toResponse(PromoCode promoCode);

    @Mapping(target = "validForTickets", ignore = true)
    PromoCode toEntity(PromoCodeRequest request);
}
