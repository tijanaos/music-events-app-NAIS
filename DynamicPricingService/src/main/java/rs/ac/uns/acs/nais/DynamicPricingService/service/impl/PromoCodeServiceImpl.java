package rs.ac.uns.acs.nais.DynamicPricingService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.PromoCodeRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.PromoCodeResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.exception.ResourceNotFoundException;
import rs.ac.uns.acs.nais.DynamicPricingService.mapper.PromoCodeMapper;
import rs.ac.uns.acs.nais.DynamicPricingService.model.PromoCode;
import rs.ac.uns.acs.nais.DynamicPricingService.model.TicketType;
import rs.ac.uns.acs.nais.DynamicPricingService.model.relationship.ValidFor;
import rs.ac.uns.acs.nais.DynamicPricingService.repository.PromoCodeRepository;
import rs.ac.uns.acs.nais.DynamicPricingService.repository.TicketTypeRepository;
import rs.ac.uns.acs.nais.DynamicPricingService.service.PromoCodeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromoCodeServiceImpl implements PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final PromoCodeMapper promoCodeMapper;

    @Override
    public List<PromoCodeResponse> getAll() {
        return promoCodeRepository.findAll().stream()
                .map(promoCodeMapper::toResponse)
                .toList();
    }

    @Override
    public PromoCodeResponse getById(String id) {
        return promoCodeRepository.findById(id)
                .map(promoCodeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("PromoCode not found: " + id));
    }

    @Override
    public PromoCodeResponse create(PromoCodeRequest request) {
        PromoCode promoCode = promoCodeMapper.toEntity(request);
        promoCode.setValidForTickets(resolveValidFor(request));
        return promoCodeMapper.toResponse(promoCodeRepository.save(promoCode));
    }

    @Override
    public PromoCodeResponse update(String id, PromoCodeRequest request) {
        promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PromoCode not found: " + id));
        PromoCode updated = promoCodeMapper.toEntity(request);
        updated.setCode(id);
        updated.setValidForTickets(resolveValidFor(request));
        return promoCodeMapper.toResponse(promoCodeRepository.save(updated));
    }

    @Override
    public void delete(String id) {
        promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PromoCode not found: " + id));
        promoCodeRepository.deleteById(id);
    }

    private List<ValidFor> resolveValidFor(PromoCodeRequest request) {
        if (request.getValidForTickets() == null) return List.of();
        return request.getValidForTickets().stream()
                .map(vfr -> {
                    TicketType tt = ticketTypeRepository.findById(vfr.getTicketTypeId())
                            .orElseThrow(() -> new ResourceNotFoundException("TicketType not found: " + vfr.getTicketTypeId()));
                    return new ValidFor(null, vfr.getMinQuantity(), tt);
                })
                .toList();
    }
}
