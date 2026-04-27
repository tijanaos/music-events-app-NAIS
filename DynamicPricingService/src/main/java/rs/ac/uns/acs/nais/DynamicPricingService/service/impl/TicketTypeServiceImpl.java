package rs.ac.uns.acs.nais.DynamicPricingService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.TicketTypeRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.TicketTypeResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.exception.ResourceNotFoundException;
import rs.ac.uns.acs.nais.DynamicPricingService.mapper.TicketTypeMapper;
import rs.ac.uns.acs.nais.DynamicPricingService.model.TicketType;
import rs.ac.uns.acs.nais.DynamicPricingService.repository.TicketTypeRepository;
import rs.ac.uns.acs.nais.DynamicPricingService.service.TicketTypeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {

    private final TicketTypeRepository ticketTypeRepository;
    private final TicketTypeMapper ticketTypeMapper;

    @Override
    public List<TicketTypeResponse> getAll() {
        return ticketTypeRepository.findAll().stream()
                .map(ticketTypeMapper::toResponse)
                .toList();
    }

    @Override
    public TicketTypeResponse getById(String id) {
        return ticketTypeRepository.findById(id)
                .map(ticketTypeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("TicketType not found: " + id));
    }

    @Override
    public TicketTypeResponse create(TicketTypeRequest request) {
        TicketType saved = ticketTypeRepository.save(ticketTypeMapper.toEntity(request));
        return ticketTypeMapper.toResponse(saved);
    }

    @Override
    public TicketTypeResponse update(String id, TicketTypeRequest request) {
        ticketTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TicketType not found: " + id));
        TicketType updated = ticketTypeMapper.toEntity(request);
        updated.setTicketTypeId(id);
        return ticketTypeMapper.toResponse(ticketTypeRepository.save(updated));
    }

    @Override
    public void delete(String id) {
        ticketTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TicketType not found: " + id));
        ticketTypeRepository.deleteById(id);
    }
}
