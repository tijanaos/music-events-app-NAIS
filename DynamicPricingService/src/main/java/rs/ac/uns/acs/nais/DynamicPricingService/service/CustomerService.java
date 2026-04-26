package rs.ac.uns.acs.nais.DynamicPricingService.service;

import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.CustomerRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {

    List<CustomerResponse> getAll();
    CustomerResponse getById(String id);
    CustomerResponse create(CustomerRequest request);
    CustomerResponse update(String id, CustomerRequest request);
    void delete(String id);
}
