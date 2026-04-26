package rs.ac.uns.acs.nais.DynamicPricingService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.CustomerRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.CustomerResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.exception.ResourceNotFoundException;
import rs.ac.uns.acs.nais.DynamicPricingService.mapper.CustomerMapper;
import rs.ac.uns.acs.nais.DynamicPricingService.model.Customer;
import rs.ac.uns.acs.nais.DynamicPricingService.repository.CustomerRepository;
import rs.ac.uns.acs.nais.DynamicPricingService.service.CustomerService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerResponse> getAll() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    @Override
    public CustomerResponse getById(String id) {
        return customerRepository.findById(id)
                .map(customerMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
    }

    @Override
    public CustomerResponse create(CustomerRequest request) {
        Customer saved = customerRepository.save(customerMapper.toEntity(request));
        return customerMapper.toResponse(saved);
    }

    @Override
    public CustomerResponse update(String id, CustomerRequest request) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        Customer updated = customerMapper.toEntity(request);
        updated.setCustomerId(id);
        updated.setPurchases(existing.getPurchases());
        return customerMapper.toResponse(customerRepository.save(updated));
    }

    @Override
    public void delete(String id) {
        customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        customerRepository.deleteById(id);
    }
}
