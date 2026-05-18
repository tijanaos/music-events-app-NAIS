package rs.ac.uns.acs.nais.AdvertisingSearchService.service;

import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdPhaseSearchQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdTypeSearchQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.ApprovalSummaryQueryResponse;

import java.util.List;

public interface AdvertisingAnalyticsService {
    AdTypeSearchQueryResponse searchActiveAdTypes(String text, String category, String contentType);
    AdPhaseSearchQueryResponse findNotificationPhases(String adTypeName, int minimumDurationHours);
    AdPhaseSearchQueryResponse searchPhaseWorkflowText(String text, boolean activeOnly);
    ApprovalSummaryQueryResponse findApprovalHeavyAdTypes(List<String> categories, int minDurationDays, int maxDurationDays);
}
