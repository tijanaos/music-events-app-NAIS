package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service;

import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request.ResourceUsageDto;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ResourceUsageResponse;

import java.time.LocalDate;
import java.util.List;

public interface ResourceUsageService {

    ResourceUsageResponse create(ResourceUsageDto dto);

    ResourceUsageResponse findById(String id);

    List<ResourceUsageResponse> findAll();

    List<ResourceUsageResponse> findByStageId(String stageId);

    List<ResourceUsageResponse> findByResourceId(String resourceId);

    List<ResourceUsageResponse> findByResourceType(String resourceType);

    List<ResourceUsageResponse> findByPeriod(LocalDate from, LocalDate to);

    List<ResourceUsageResponse> findBorrowedResources();

    List<ResourceUsageResponse> findByReservationId(String reservationId);

    ResourceUsageResponse update(String id, ResourceUsageDto dto);

    void delete(String id);
}
