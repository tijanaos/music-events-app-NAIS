package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.config.CacheNames;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request.ReservationRequestDto;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request.RequestedResourceItemRequest;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ReservationRequestResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.exception.ResourceNotFoundException;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ReservationRequestDocument;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.RequestedResourceItem;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.repository.ReservationRequestRepository;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service.ReservationRequestService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ReservationRequestServiceImpl implements ReservationRequestService {

    private final ReservationRequestRepository repository;

    @Override
    @CacheEvict(cacheNames = {
            CacheNames.RESERVATION_SEARCH,
            CacheNames.MOST_USED_RESOURCES_BY_STAGE,
            CacheNames.TIME_SLOTS_WITH_MOST_RESOURCES,
            CacheNames.RESERVATIONS_WITH_MISSING_RESOURCES,
            CacheNames.RESOURCE_UTILIZATION_REPORTS
    }, allEntries = true)
    public ReservationRequestResponse create(ReservationRequestDto dto) {
        ReservationRequestDocument doc = toDocument(dto);
        doc.setId(UUID.randomUUID().toString());
        return toResponse(repository.save(doc));
    }

    @Override
    public ReservationRequestResponse findById(String id) {
        return toResponse(getOrThrow(id));
    }

    @Override
    public List<ReservationRequestResponse> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationRequestResponse> findByStageId(String stageId) {
        return repository.findByStageId(stageId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationRequestResponse> findByStatus(String status) {
        return repository.findByRequestStatus(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationRequestResponse> findByPerformerId(String performerId) {
        return repository.findByPerformerId(performerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationRequestResponse> findWithTasks() {
        return repository.findByHasTasksTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(cacheNames = {
            CacheNames.RESERVATION_SEARCH,
            CacheNames.MOST_USED_RESOURCES_BY_STAGE,
            CacheNames.TIME_SLOTS_WITH_MOST_RESOURCES,
            CacheNames.RESERVATIONS_WITH_MISSING_RESOURCES,
            CacheNames.RESOURCE_UTILIZATION_REPORTS
    }, allEntries = true)
    public ReservationRequestResponse update(String id, ReservationRequestDto dto) {
        ReservationRequestDocument existing = getOrThrow(id);
        ReservationRequestDocument updated = toDocument(dto);
        updated.setId(existing.getId());
        return toResponse(repository.save(updated));
    }

    @Override
    @CacheEvict(cacheNames = {
            CacheNames.RESERVATION_SEARCH,
            CacheNames.MOST_USED_RESOURCES_BY_STAGE,
            CacheNames.TIME_SLOTS_WITH_MOST_RESOURCES,
            CacheNames.RESERVATIONS_WITH_MISSING_RESOURCES,
            CacheNames.RESOURCE_UTILIZATION_REPORTS
    }, allEntries = true)
    public void delete(String id) {
        getOrThrow(id);
        repository.deleteById(id);
    }

    private ReservationRequestDocument getOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation request not found: " + id));
    }

    private ReservationRequestDocument toDocument(ReservationRequestDto dto) {
        List<RequestedResourceItem> resources = dto.getRequestedResources() == null ? List.of() :
                dto.getRequestedResources().stream()
                        .map(this::toResourceItem)
                        .collect(Collectors.toList());

        return ReservationRequestDocument.builder()
                .requestStatus(dto.getRequestStatus())
                .sentDate(dto.getSentDate())
                .updatedDate(dto.getUpdatedDate())
                .note(dto.getNote())
                .stageId(dto.getStageId())
                .stageName(dto.getStageName())
                .stageType(dto.getStageType())
                .stageCapacity(dto.getStageCapacity())
                .performerId(dto.getPerformerId())
                .performerFirstName(dto.getPerformerFirstName())
                .performerLastName(dto.getPerformerLastName())
                .genre(dto.getGenre())
                .popularity(dto.getPopularity())
                .performanceDate(dto.getPerformanceDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .requestedResources(resources)
                .hasTasks(dto.getHasTasks())
                .taskCount(dto.getTaskCount())
                .performanceDetails(dto.getPerformanceDetails())
                .build();
    }

    private RequestedResourceItem toResourceItem(RequestedResourceItemRequest req) {
        return RequestedResourceItem.builder()
                .resourceName(req.getResourceName())
                .resourceType(req.getResourceType())
                .requestedQuantity(req.getRequestedQuantity())
                .existsInSystem(req.getExistsInSystem())
                .resourceStatus(req.getResourceStatus())
                .rejectionReason(req.getRejectionReason())
                .build();
    }

    private ReservationRequestResponse toResponse(ReservationRequestDocument doc) {
        return ReservationRequestResponse.builder()
                .id(doc.getId())
                .requestStatus(doc.getRequestStatus())
                .sentDate(doc.getSentDate())
                .updatedDate(doc.getUpdatedDate())
                .note(doc.getNote())
                .stageId(doc.getStageId())
                .stageName(doc.getStageName())
                .stageType(doc.getStageType())
                .stageCapacity(doc.getStageCapacity())
                .performerId(doc.getPerformerId())
                .performerFirstName(doc.getPerformerFirstName())
                .performerLastName(doc.getPerformerLastName())
                .genre(doc.getGenre())
                .popularity(doc.getPopularity())
                .performanceDate(doc.getPerformanceDate())
                .startTime(doc.getStartTime())
                .endTime(doc.getEndTime())
                .requestedResources(doc.getRequestedResources())
                .hasTasks(doc.getHasTasks())
                .taskCount(doc.getTaskCount())
                .performanceDetails(doc.getPerformanceDetails())
                .build();
    }
}
