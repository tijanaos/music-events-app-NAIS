package rs.ac.uns.acs.nais.AdvertisingSearchService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.request.AdTypeRequest;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdTypeResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.exception.ResourceNotFoundException;
import rs.ac.uns.acs.nais.AdvertisingSearchService.model.AdTypeDocument;
import rs.ac.uns.acs.nais.AdvertisingSearchService.repository.AdTypeRepository;
import rs.ac.uns.acs.nais.AdvertisingSearchService.service.AdTypeService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AdTypeServiceImpl implements AdTypeService {

    private final AdTypeRepository adTypeRepository;

    @Override
    public List<AdTypeResponse> getAll() {
        return StreamSupport.stream(adTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).spliterator(), false)
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AdTypeResponse getById(Long id) {
        return adTypeRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Ad type not found: " + id));
    }

    @Override
    public AdTypeResponse create(AdTypeRequest request) {
        AdTypeDocument saved = adTypeRepository.save(mapToDocument(request, request.getId() != null ? request.getId() : nextId()));
        return mapToResponse(saved);
    }

    @Override
    public AdTypeResponse update(Long id, AdTypeRequest request) {
        adTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ad type not found: " + id));
        AdTypeDocument saved = adTypeRepository.save(mapToDocument(request, id));
        return mapToResponse(saved);
    }

    @Override
    public void delete(Long id) {
        adTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ad type not found: " + id));
        adTypeRepository.deleteById(id);
    }

    private Long nextId() {
        return StreamSupport.stream(adTypeRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).spliterator(), false)
                .findFirst()
                .map(AdTypeDocument::getId)
                .map(id -> id + 1)
                .orElse(1L);
    }

    private AdTypeDocument mapToDocument(AdTypeRequest request, Long id) {
        return AdTypeDocument.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .contentType(request.getContentType())
                .category(request.getCategory())
                .targetChannel(request.getTargetChannel())
                .isActive(request.getIsActive())
                .requiresApproval(request.getRequiresApproval())
                .averageDurationDays(request.getAverageDurationDays())
                .createdAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDate.now())
                .build();
    }

    private AdTypeResponse mapToResponse(AdTypeDocument document) {
        return AdTypeResponse.builder()
                .id(document.getId())
                .name(document.getName())
                .description(document.getDescription())
                .contentType(document.getContentType())
                .category(document.getCategory())
                .targetChannel(document.getTargetChannel())
                .isActive(document.getIsActive())
                .requiresApproval(document.getRequiresApproval())
                .averageDurationDays(document.getAverageDurationDays())
                .createdAt(document.getCreatedAt())
                .build();
    }
}
