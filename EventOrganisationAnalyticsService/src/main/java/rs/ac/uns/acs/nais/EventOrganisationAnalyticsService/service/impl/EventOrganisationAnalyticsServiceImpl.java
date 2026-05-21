package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
    public ReservationSearchQueryResponse searchReservationsByPerformerText(
            String searchText, String status, String zanr) throws IOException {
        return analyticsRepository.searchReservationsByPerformerText(searchText, status, zanr);
    }

    @Override
    public List<ResourceUsageByStageResponse> getMostUsedResourcesByStage() throws IOException {
        return analyticsRepository.getMostUsedResourcesByStage();
    }

    @Override
    public List<AggregationBucketResponse> getTimeSlotsWithMostResources(
            LocalDate from, LocalDate to) throws IOException {
        return analyticsRepository.getTimeSlotsWithMostResources(from, to);
    }

    @Override
    public List<AggregationBucketResponse> getReservationsWithMissingResourcesByStage() throws IOException {
        return analyticsRepository.getReservationsWithMissingResourcesByStage();
    }

    @Override
    public ResourceUtilizationReportResponse getResourceUtilizationReport(
            LocalDate from, LocalDate to, String binaId) throws IOException {
        return analyticsRepository.getResourceUtilizationReport(from, to, binaId);
    }
}
