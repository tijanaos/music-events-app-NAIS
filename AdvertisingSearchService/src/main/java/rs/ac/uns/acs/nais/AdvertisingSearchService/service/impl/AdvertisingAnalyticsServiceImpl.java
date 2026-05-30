package rs.ac.uns.acs.nais.AdvertisingSearchService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdPhaseSearchQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdTypeSearchQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.ApprovalSummaryQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.repository.AdvertisingAnalyticsRepository;
import rs.ac.uns.acs.nais.AdvertisingSearchService.service.AdvertisingAnalyticsService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertisingAnalyticsServiceImpl implements AdvertisingAnalyticsService {

    private final AdvertisingAnalyticsRepository advertisingAnalyticsRepository;

    @Override
    public AdTypeSearchQueryResponse searchActiveAdTypes(String text, String category, String contentType) {
        return advertisingAnalyticsRepository.searchActiveAdTypes(text, category, contentType);
    }

    @Override
    public AdPhaseSearchQueryResponse findNotificationPhases(String adTypeName, int minimumDurationHours) {
        return advertisingAnalyticsRepository.findNotificationPhases(adTypeName, minimumDurationHours);
    }

    @Override
    public AdPhaseSearchQueryResponse searchPhaseWorkflowText(String text, boolean activeOnly) {
        return advertisingAnalyticsRepository.searchPhaseWorkflowText(text, activeOnly);
    }

    @Override
    public ApprovalSummaryQueryResponse findApprovalHeavyAdTypes(List<String> categories, int minDurationDays, int maxDurationDays) {
        return advertisingAnalyticsRepository.findApprovalHeavyAdTypes(categories, minDurationDays, maxDurationDays);
    }
}
