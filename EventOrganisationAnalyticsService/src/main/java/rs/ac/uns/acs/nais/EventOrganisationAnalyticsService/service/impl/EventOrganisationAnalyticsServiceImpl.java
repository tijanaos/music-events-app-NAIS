package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.config.CacheNames;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.AggregationBucketResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ReservationSearchQueryResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ResourceUsageByStageResponse;
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
    public ReservationSearchQueryResponse searchReservationsByPerformerText(
            String searchText, String status, String genre) throws IOException {
        return analyticsRepository.searchReservationsByPerformerText(searchText, status, genre);
    }

    @Override
    @Cacheable(
            cacheNames = CacheNames.MOST_USED_RESOURCES_BY_STAGE,
            key = "T(rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.config.CachePolicy).normalizeResourceType(#resourceType)")
    public List<ResourceUsageByStageResponse> getMostUsedResourcesByStage(String resourceType) throws IOException {
        return analyticsRepository.getMostUsedResourcesByStage(resourceType);
    }

    @Override
    @Cacheable(
            cacheNames = CacheNames.PEAK_RESOURCE_HOURS,
            key = "T(rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.config.CachePolicy).standardReportingPeriodKey(#from, #to) + ':' + T(rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.config.CachePolicy).normalizeResourceType(#resourceType)",
            condition = "T(rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.config.CachePolicy).isStandardReportingPeriod(#from, #to)")
    public List<AggregationBucketResponse> getPeakResourceHours(
            LocalDate from, LocalDate to, String resourceType) throws IOException {
        return analyticsRepository.getPeakResourceHours(from, to, resourceType);
    }
}
