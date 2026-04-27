package rs.ac.uns.acs.nais.DynamicPricingService.service;

import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.TicketTypeRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.TicketTypeResponse;

import java.util.List;

public interface TicketTypeService {

    List<TicketTypeResponse> getAll();
    TicketTypeResponse getById(String id);
    TicketTypeResponse create(TicketTypeRequest request);
    TicketTypeResponse update(String id, TicketTypeRequest request);
    void delete(String id);
}
