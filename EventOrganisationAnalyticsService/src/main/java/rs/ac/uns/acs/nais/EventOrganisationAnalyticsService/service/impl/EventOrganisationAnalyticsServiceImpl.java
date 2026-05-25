package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.config.CacheNames;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.AggregationBucketResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ReservationSearchQueryResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ResourceUsageByStageResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ResourceUtilizationReportResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.repository.EventOrganisationAnalyticsRepository;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service.EventOrganisationAnalyticsService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventOrganisationAnalyticsServiceImpl implements EventOrganisationAnalyticsService {

    private final EventOrganisationAnalyticsRepository analyticsRepository;

    @Override
    @Cacheable(
            cacheNames = CacheNames.RESERVATION_SEARCH,
            key = "#searchText + ':' + (#status == null || #status.isBlank() ? 'all' : #status) + ':' + (#genre == null || #genre.isBlank() ? 'all' : #genre)")
    public ReservationSearchQueryResponse searchReservationsByPerformerText(
            String searchText, String status, String genre) throws IOException {
        return analyticsRepository.searchReservationsByPerformerText(searchText, status, genre);
    }

    @Override
    @Cacheable(cacheNames = CacheNames.MOST_USED_RESOURCES_BY_STAGE)
    public List<ResourceUsageByStageResponse> getMostUsedResourcesByStage() throws IOException {
        return analyticsRepository.getMostUsedResourcesByStage();
    }

    @Override
    @Cacheable(
            cacheNames = CacheNames.TIME_SLOTS_WITH_MOST_RESOURCES,
            key = "#from + ':' + #to")
    public List<AggregationBucketResponse> getTimeSlotsWithMostResources(
            LocalDate from, LocalDate to) throws IOException {
        return analyticsRepository.getTimeSlotsWithMostResources(from, to);
    }

    @Override
    @Cacheable(cacheNames = CacheNames.RESERVATIONS_WITH_MISSING_RESOURCES)
    public List<AggregationBucketResponse> getReservationsWithMissingResourcesByStage() throws IOException {
        return analyticsRepository.getReservationsWithMissingResourcesByStage();
    }

    @Override
    @Cacheable(
            cacheNames = CacheNames.RESOURCE_UTILIZATION_REPORTS,
            key = "#from + ':' + #to + ':' + (#stageId == null || #stageId.isBlank() ? 'all' : #stageId)")
    public ResourceUtilizationReportResponse getResourceUtilizationReport(
            LocalDate from, LocalDate to, String stageId) throws IOException {
        return analyticsRepository.getResourceUtilizationReport(from, to, stageId);
    }
}
