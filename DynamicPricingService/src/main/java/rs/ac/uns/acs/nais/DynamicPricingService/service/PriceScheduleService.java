package rs.ac.uns.acs.nais.DynamicPricingService.service;

import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.PriceScheduleRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.PriceScheduleResponse;

import java.util.List;

public interface PriceScheduleService {

    List<PriceScheduleResponse> getAll();
    PriceScheduleResponse getById(String id);
    PriceScheduleResponse create(PriceScheduleRequest request);
    PriceScheduleResponse update(String id, PriceScheduleRequest request);
    void delete(String id);
}
