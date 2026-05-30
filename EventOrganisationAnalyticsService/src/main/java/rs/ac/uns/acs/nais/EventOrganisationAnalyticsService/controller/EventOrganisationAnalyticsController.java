package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.AggregationBucketResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ReservationSearchQueryResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ResourceUsageByStageResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service.EventOrganisationAnalyticsService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class EventOrganisationAnalyticsController {

    private final EventOrganisationAnalyticsService analyticsService;

    @GetMapping("/search-reservations")
    public ResponseEntity<ReservationSearchQueryResponse> searchReservations(
            @RequestParam String searchText,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String genre) throws IOException {
        return ResponseEntity.ok(analyticsService.searchReservationsByPerformerText(searchText, status, genre));
    }

    @GetMapping("/most-used-resources-by-stage")
    public ResponseEntity<List<ResourceUsageByStageResponse>> getMostUsedResourcesByStage(
            @RequestParam String resourceType) throws IOException {
        return ResponseEntity.ok(analyticsService.getMostUsedResourcesByStage(resourceType));
    }

    @GetMapping("/peak-resource-hours")
    public ResponseEntity<List<AggregationBucketResponse>> getPeakResourceHours(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam String resourceType) throws IOException {
        return ResponseEntity.ok(analyticsService.getPeakResourceHours(from, to, resourceType));
    }
}
