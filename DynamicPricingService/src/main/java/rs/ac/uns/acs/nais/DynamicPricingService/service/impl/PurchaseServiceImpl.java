package rs.ac.uns.acs.nais.DynamicPricingService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.PurchaseRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.PurchaseResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.exception.ResourceNotFoundException;
import rs.ac.uns.acs.nais.DynamicPricingService.mapper.PurchaseMapper;
import rs.ac.uns.acs.nais.DynamicPricingService.model.Purchase;
import rs.ac.uns.acs.nais.DynamicPricingService.repository.PromoCodeRepository;
import rs.ac.uns.acs.nais.DynamicPricingService.repository.PurchaseRepository;
import rs.ac.uns.acs.nais.DynamicPricingService.repository.TicketTypeRepository;
import rs.ac.uns.acs.nais.DynamicPricingService.service.PurchaseService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final PurchaseMapper purchaseMapper;

    @Override
    public List<PurchaseResponse> getAll() {
        return purchaseRepository.findAll().stream()
                .map(purchaseMapper::toResponse)
                .toList();
    }

    @Override
    public PurchaseResponse getById(String id) {
        return purchaseRepository.findById(id)
                .map(purchaseMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found: " + id));
    }

    @Override
    public PurchaseResponse create(PurchaseRequest request) {
        Purchase purchase = buildPurchase(request);
        return purchaseMapper.toResponse(purchaseRepository.save(purchase));
    }

    @Override
    public PurchaseResponse update(String id, PurchaseRequest request) {
        purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found: " + id));
        Purchase purchase = buildPurchase(request);
        purchase.setPurchaseId(id);
        return purchaseMapper.toResponse(purchaseRepository.save(purchase));
    }

    @Override
    public void delete(String id) {
        purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found: " + id));
        purchaseRepository.deleteById(id);
    }

    private Purchase buildPurchase(PurchaseRequest request) {
        Purchase purchase = purchaseMapper.toEntity(request);

        purchase.setTicketType(
                ticketTypeRepository.findById(request.getTicketTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("TicketType not found: " + request.getTicketTypeId()))
        );

        if (request.getPromoCode() != null) {
            purchase.setPromoCode(
                    promoCodeRepository.findById(request.getPromoCode())
                            .orElseThrow(() -> new ResourceNotFoundException("PromoCode not found: " + request.getPromoCode()))
            );
        }

        return purchase;
    }
}
