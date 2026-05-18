package rs.ac.uns.acs.nais.AdvertisingSearchService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdPhaseSearchQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdTypeSearchQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.ApprovalSummaryQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.service.AdvertisingAnalyticsService;

import java.util.List;

@RestController
@RequestMapping("/api/advertising-queries")
@RequiredArgsConstructor
public class AdvertisingAnalyticsController {

    private final AdvertisingAnalyticsService advertisingAnalyticsService;

    @GetMapping("/active-ad-types")
    public ResponseEntity<AdTypeSearchQueryResponse> searchActiveAdTypes(
            @RequestParam(defaultValue = "video promocija") String text,
            @RequestParam(defaultValue = "festival") String category,
            @RequestParam(defaultValue = "video") String contentType) {
        return ResponseEntity.ok(advertisingAnalyticsService.searchActiveAdTypes(text, category, contentType));
    }

    @GetMapping("/notification-phases")
    public ResponseEntity<AdPhaseSearchQueryResponse> findNotificationPhases(
            @RequestParam(defaultValue = "Video oglas 1") String adTypeName,
            @RequestParam(defaultValue = "12") int minimumDurationHours) {
        return ResponseEntity.ok(advertisingAnalyticsService.findNotificationPhases(adTypeName, minimumDurationHours));
    }

    @GetMapping("/phase-text-search")
    public ResponseEntity<AdPhaseSearchQueryResponse> searchPhaseWorkflowText(
            @RequestParam(defaultValue = "provera odobrenje validacija") String text,
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        return ResponseEntity.ok(advertisingAnalyticsService.searchPhaseWorkflowText(text, activeOnly));
    }

    @GetMapping("/approval-summary")
    public ResponseEntity<ApprovalSummaryQueryResponse> findApprovalHeavyAdTypes(
            @RequestParam(defaultValue = "festival,sponsor,performer") List<String> categories,
            @RequestParam(defaultValue = "7") int minDurationDays,
            @RequestParam(defaultValue = "30") int maxDurationDays) {
        return ResponseEntity.ok(advertisingAnalyticsService.findApprovalHeavyAdTypes(categories, minDurationDays, maxDurationDays));
    }
}
