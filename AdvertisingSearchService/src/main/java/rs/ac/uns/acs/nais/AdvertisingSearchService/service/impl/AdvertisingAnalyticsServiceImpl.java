package rs.ac.uns.acs.nais.AdvertisingSearchService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdPhaseSearchQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdTypeSearchQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.ApprovalSummaryQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.repository.AdvertisingAnalyticsRepository;
import rs.ac.uns.acs.nais.AdvertisingSearchService.service.AdvertisingAnalyticsService;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertisingAnalyticsServiceImpl implements AdvertisingAnalyticsService {

    private final AdvertisingAnalyticsRepository advertisingAnalyticsRepository;

    @Override
    public AdTypeSearchQueryResponse searchActiveAdTypes(String text, String category, String contentType) {
        try {
            return advertisingAnalyticsRepository.searchActiveAdTypes(text, category, contentType);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public AdPhaseSearchQueryResponse findNotificationPhases(String adTypeName, int minimumDurationHours) {
        try {
            return advertisingAnalyticsRepository.findNotificationPhases(adTypeName, minimumDurationHours);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public AdPhaseSearchQueryResponse searchPhaseWorkflowText(String text, boolean activeOnly) {
        try {
            return advertisingAnalyticsRepository.searchPhaseWorkflowText(text, activeOnly);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public ApprovalSummaryQueryResponse findApprovalHeavyAdTypes(List<String> categories, int minDurationDays, int maxDurationDays) {
        try {
            return advertisingAnalyticsRepository.findApprovalHeavyAdTypes(categories, minDurationDays, maxDurationDays);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
