package rs.ac.uns.acs.nais.DynamicPricingService.mapper;

import org.mapstruct.Mapper;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.TicketTypeRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.TicketTypeResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.model.TicketType;

@Mapper(componentModel = "spring", uses = {PriceScheduleMapper.class})
public interface TicketTypeMapper {

    TicketTypeResponse toResponse(TicketType ticketType);

    TicketType toEntity(TicketTypeRequest request);
}
