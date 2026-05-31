package rs.ac.uns.acs.nais.AdvertisingSearchService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.request.AdPhaseRequest;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdPhaseResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.exception.ResourceNotFoundException;
import rs.ac.uns.acs.nais.AdvertisingSearchService.model.AdPhaseDocument;
import rs.ac.uns.acs.nais.AdvertisingSearchService.repository.AdPhaseRepository;
import rs.ac.uns.acs.nais.AdvertisingSearchService.repository.AdTypeRepository;
import rs.ac.uns.acs.nais.AdvertisingSearchService.service.AdPhaseService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AdPhaseServiceImpl implements AdPhaseService {

    private final AdPhaseRepository adPhaseRepository;
    private final AdTypeRepository adTypeRepository;

    @Override
    @Cacheable("adPhasesAll")
    public List<AdPhaseResponse> getAll() {
        return StreamSupport.stream(adPhaseRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).spliterator(), false)
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AdPhaseResponse getById(Long id) {
        return adPhaseRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Ad phase not found: " + id));
    }

    @Override
    @CacheEvict(value = "adPhasesAll", allEntries = true)
    public AdPhaseResponse create(AdPhaseRequest request) {
        ensureAdTypeExists(request.getAdTypeId());
        AdPhaseDocument saved = adPhaseRepository.save(mapToDocument(request, request.getId() != null ? request.getId() : nextId()));
        return mapToResponse(saved);
    }

    @Override
    @CacheEvict(value = "adPhasesAll", allEntries = true)
    public AdPhaseResponse update(Long id, AdPhaseRequest request) {
        adPhaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ad phase not found: " + id));
        ensureAdTypeExists(request.getAdTypeId());
        AdPhaseDocument saved = adPhaseRepository.save(mapToDocument(request, id));
        return mapToResponse(saved);
    }

    @Override
    @CacheEvict(value = "adPhasesAll", allEntries = true)
    public void delete(Long id) {
        adPhaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ad phase not found: " + id));
        adPhaseRepository.deleteById(id);
    }

    private void ensureAdTypeExists(Long adTypeId) {
        adTypeRepository.findById(adTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Referenced ad type not found: " + adTypeId));
    }

    private Long nextId() {
        return StreamSupport.stream(adPhaseRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).spliterator(), false)
                .findFirst()
                .map(AdPhaseDocument::getId)
                .map(id -> id + 1)
                .orElse(1L);
    }

    private AdPhaseDocument mapToDocument(AdPhaseRequest request, Long id) {
        return AdPhaseDocument.builder()
                .id(id)
                .adTypeId(request.getAdTypeId())
                .phaseName(request.getPhaseName())
                .description(request.getDescription())
                .phaseOrder(request.getPhaseOrder())
                .responsibleRole(request.getResponsibleRole())
                .requiresEmailNotification(request.getRequiresEmailNotification())
                .isFinalPhase(request.getIsFinalPhase())
                .isActive(request.getIsActive())
                .expectedDurationHours(request.getExpectedDurationHours())
                .createdAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDate.now())
                .build();
    }

    private AdPhaseResponse mapToResponse(AdPhaseDocument document) {
        return AdPhaseResponse.builder()
                .id(document.getId())
                .adTypeId(document.getAdTypeId())
                .phaseName(document.getPhaseName())
                .description(document.getDescription())
                .phaseOrder(document.getPhaseOrder())
                .responsibleRole(document.getResponsibleRole())
                .requiresEmailNotification(document.getRequiresEmailNotification())
                .isFinalPhase(document.getIsFinalPhase())
                .isActive(document.getIsActive())
                .expectedDurationHours(document.getExpectedDurationHours())
                .createdAt(document.getCreatedAt())
                .build();
    }
}
