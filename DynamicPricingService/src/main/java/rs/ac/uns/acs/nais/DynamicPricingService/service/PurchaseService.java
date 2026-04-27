package rs.ac.uns.acs.nais.DynamicPricingService.service;

import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.PurchaseRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.PurchaseResponse;

import java.util.List;

public interface PurchaseService {

    List<PurchaseResponse> getAll();
    PurchaseResponse getById(String id);
    PurchaseResponse create(PurchaseRequest request);
    PurchaseResponse update(String id, PurchaseRequest request);
    void delete(String id);
}
