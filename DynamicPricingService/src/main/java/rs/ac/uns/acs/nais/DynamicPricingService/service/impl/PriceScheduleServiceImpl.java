package rs.ac.uns.acs.nais.DynamicPricingService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.PriceScheduleRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.PriceScheduleResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.exception.ResourceNotFoundException;
import rs.ac.uns.acs.nais.DynamicPricingService.mapper.PriceScheduleMapper;
import rs.ac.uns.acs.nais.DynamicPricingService.model.PriceSchedule;
import rs.ac.uns.acs.nais.DynamicPricingService.model.TicketType;
import rs.ac.uns.acs.nais.DynamicPricingService.repository.PriceScheduleRepository;
import rs.ac.uns.acs.nais.DynamicPricingService.repository.TicketTypeRepository;
import rs.ac.uns.acs.nais.DynamicPricingService.service.PriceScheduleService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PriceScheduleServiceImpl implements PriceScheduleService {

    private final PriceScheduleRepository priceScheduleRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final PriceScheduleMapper priceScheduleMapper;

    @Override
    public List<PriceScheduleResponse> getAll() {
        return priceScheduleRepository.findAll().stream()
                .map(priceScheduleMapper::toResponse)
                .toList();
    }

    @Override
    public PriceScheduleResponse getById(String id) {
        return priceScheduleRepository.findById(id)
                .map(priceScheduleMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("PriceSchedule not found: " + id));
    }

    @Override
    public PriceScheduleResponse create(PriceScheduleRequest request) {
        TicketType ticketType = ticketTypeRepository.findById(request.getTicketTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("TicketType not found: " + request.getTicketTypeId()));

        PriceSchedule schedule = priceScheduleMapper.toEntity(request);
        schedule.setId(UUID.randomUUID().toString());
        PriceSchedule saved = priceScheduleRepository.save(schedule);

        ticketType.getSchedules().add(saved);
        ticketTypeRepository.save(ticketType);

        return priceScheduleMapper.toResponse(saved);
    }

    @Override
    public PriceScheduleResponse update(String id, PriceScheduleRequest request) {
        priceScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PriceSchedule not found: " + id));

        PriceSchedule updated = priceScheduleMapper.toEntity(request);
        updated.setId(id);
        return priceScheduleMapper.toResponse(priceScheduleRepository.save(updated));
    }

    @Override
    public void delete(String id) {
        priceScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PriceSchedule not found: " + id));
        priceScheduleRepository.deleteById(id);
    }
}
