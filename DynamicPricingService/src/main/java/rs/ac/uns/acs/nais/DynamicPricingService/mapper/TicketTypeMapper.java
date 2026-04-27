package rs.ac.uns.acs.nais.DynamicPricingService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.TicketTypeRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.TicketTypeResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.model.TicketType;

@Mapper(componentModel = "spring", uses = {PriceScheduleMapper.class})
public interface TicketTypeMapper {

    @Mapping(target = "ticketTypeId", source = "id")
    TicketTypeResponse toResponse(TicketType ticketType);

    @Mapping(target = "id", ignore = true)
    TicketType toEntity(TicketTypeRequest request);
}
