package rs.ac.uns.acs.nais.DynamicPricingService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.PriceScheduleRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.PriceScheduleResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.model.PriceSchedule;

@Mapper(componentModel = "spring")
public interface PriceScheduleMapper {

    PriceScheduleResponse toResponse(PriceSchedule priceSchedule);

    @Mapping(target = "scheduleId", source = "scheduleId")
    PriceSchedule toEntity(PriceScheduleRequest request);
}
