package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service;

import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.AggregationBucketResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ReservationSearchQueryResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ResourceUsageByStageResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface EventOrganisationAnalyticsService {

    ReservationSearchQueryResponse searchReservationsByPerformerText(
            String searchText, String status, String genre) throws IOException;

    List<ResourceUsageByStageResponse> getMostUsedResourcesByStage(String resourceType) throws IOException;

    List<AggregationBucketResponse> getPeakResourceHours(
            LocalDate from, LocalDate to, String resourceType) throws IOException;
}
