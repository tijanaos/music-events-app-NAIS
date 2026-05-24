package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service;

import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.AggregationBucketResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ReservationSearchQueryResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ResourceUsageByStageResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ResourceUtilizationReportResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface EventOrganisationAnalyticsService {

    ReservationSearchQueryResponse searchReservationsByPerformerText(
            String searchText, String status, String genre) throws IOException;

    List<ResourceUsageByStageResponse> getMostUsedResourcesByStage() throws IOException;

    List<AggregationBucketResponse> getTimeSlotsWithMostResources(
            LocalDate from, LocalDate to) throws IOException;

    List<AggregationBucketResponse> getReservationsWithMissingResourcesByStage() throws IOException;

    ResourceUtilizationReportResponse getResourceUtilizationReport(
            LocalDate from, LocalDate to, String stageId) throws IOException;
}
