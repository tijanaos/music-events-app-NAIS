package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.config.CacheNames;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request.ResourceUsageDto;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ResourceUsageResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.exception.ResourceNotFoundException;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ResourceUsageDocument;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.repository.ResourceUsageRepository;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service.ResourceUsageService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ResourceUsageServiceImpl implements ResourceUsageService {

    private final ResourceUsageRepository repository;

    @Override
    @CacheEvict(cacheNames = {
            CacheNames.MOST_USED_RESOURCES_BY_STAGE,
            CacheNames.PEAK_RESOURCE_HOURS
    }, allEntries = true)
    public ResourceUsageResponse create(ResourceUsageDto dto) {
        ResourceUsageDocument doc = toDocument(dto);
        doc.setId(UUID.randomUUID().toString());
        return toResponse(repository.save(doc));
    }

    @Override
    public ResourceUsageResponse findById(String id) {
        return toResponse(getOrThrow(id));
    }

    @Override
    public List<ResourceUsageResponse> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceUsageResponse> findByStageId(String stageId) {
        return repository.findByStageId(stageId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceUsageResponse> findByResourceId(String resourceId) {
        return repository.findByResourceId(resourceId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceUsageResponse> findByResourceType(String resourceType) {
        return repository.findByResourceType(resourceType).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceUsageResponse> findByPeriod(LocalDate from, LocalDate to) {
        return repository.findByDateBetween(from, to).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceUsageResponse> findBorrowedResources() {
        return repository.findByBorrowedFromStageTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceUsageResponse> findByReservationId(String reservationId) {
        return repository.findByReservationId(reservationId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(cacheNames = {
            CacheNames.MOST_USED_RESOURCES_BY_STAGE,
            CacheNames.PEAK_RESOURCE_HOURS
    }, allEntries = true)
    public ResourceUsageResponse update(String id, ResourceUsageDto dto) {
        ResourceUsageDocument existing = getOrThrow(id);
        ResourceUsageDocument updated = toDocument(dto);
        updated.setId(existing.getId());
        return toResponse(repository.save(updated));
    }

    @Override
    @CacheEvict(cacheNames = {
            CacheNames.MOST_USED_RESOURCES_BY_STAGE,
            CacheNames.PEAK_RESOURCE_HOURS
    }, allEntries = true)
    public void delete(String id) {
        getOrThrow(id);
        repository.deleteById(id);
    }

    private ResourceUsageDocument getOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource usage not found: " + id));
    }

    private ResourceUsageDocument toDocument(ResourceUsageDto dto) {
        return ResourceUsageDocument.builder()
                .resourceId(dto.getResourceId())
                .resourceName(dto.getResourceName())
                .resourceType(dto.getResourceType())
                .portable(dto.getPortable())
                .allocatedQuantity(dto.getAllocatedQuantity())
                .stageId(dto.getStageId())
                .stageName(dto.getStageName())
                .stageType(dto.getStageType())
                .timeSlotId(dto.getTimeSlotId())
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .borrowedFromStage(dto.getBorrowedFromStage())
                .borrowingStageName(dto.getBorrowingStageName())
                .reservationId(dto.getReservationId())
                .build();
    }

    private ResourceUsageResponse toResponse(ResourceUsageDocument doc) {
        return ResourceUsageResponse.builder()
                .id(doc.getId())
                .resourceId(doc.getResourceId())
                .resourceName(doc.getResourceName())
                .resourceType(doc.getResourceType())
                .portable(doc.getPortable())
                .allocatedQuantity(doc.getAllocatedQuantity())
                .stageId(doc.getStageId())
                .stageName(doc.getStageName())
                .stageType(doc.getStageType())
                .timeSlotId(doc.getTimeSlotId())
                .date(doc.getDate())
                .startTime(doc.getStartTime())
                .endTime(doc.getEndTime())
                .borrowedFromStage(doc.getBorrowedFromStage())
                .borrowingStageName(doc.getBorrowingStageName())
                .reservationId(doc.getReservationId())
                .build();
    }
}
