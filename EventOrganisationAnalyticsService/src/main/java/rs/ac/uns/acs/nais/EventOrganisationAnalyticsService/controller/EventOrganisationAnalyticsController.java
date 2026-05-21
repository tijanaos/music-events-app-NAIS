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
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ResourceUtilizationReportResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service.EventOrganisationAnalyticsService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class EventOrganisationAnalyticsController {

    private final EventOrganisationAnalyticsService analyticsService;

    // Upit 1 — text search po imenu/prezimenu izvodjaca i napomeni,
    // filter po statusu i zanru, agg po statusu
    @GetMapping("/search-reservations")
    public ResponseEntity<ReservationSearchQueryResponse> searchReservations(
            @RequestParam String searchText,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String zanr) throws IOException {
        return ResponseEntity.ok(analyticsService.searchReservationsByPerformerText(searchText, status, zanr));
    }

    // Upit 2 — najkorisceniji resursi po bini sa ukupnom dodeljenom kolicinom
    @GetMapping("/most-used-resources-by-stage")
    public ResponseEntity<List<ResourceUsageByStageResponse>> getMostUsedResourcesByStage() throws IOException {
        return ResponseEntity.ok(analyticsService.getMostUsedResourcesByStage());
    }

    // Upit 3 — termini sa najvecim brojem resursa u zadatom periodu,
    // sortirani opadajuce po ukupnoj kolicini
    @GetMapping("/time-slots-with-most-resources")
    public ResponseEntity<List<AggregationBucketResponse>> getTimeSlotsWithMostResources(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) throws IOException {
        return ResponseEntity.ok(analyticsService.getTimeSlotsWithMostResources(from, to));
    }

    // Upit 4 — rezervacije koje su zahtevale resurse kojih nema u sistemu,
    // grupisane po bini sa prosecnim brojem taskova
    @GetMapping("/reservations-with-missing-resources")
    public ResponseEntity<List<AggregationBucketResponse>> getReservationsWithMissingResources() throws IOException {
        return ResponseEntity.ok(analyticsService.getReservationsWithMissingResourcesByStage());
    }

    // Upit 5 — izvestaj iskorisenosti resursa za vremenski period,
    // opciono filtriran po bini; kombinuje oba indeksa
    @GetMapping("/resource-utilization-report")
    public ResponseEntity<ResourceUtilizationReportResponse> getResourceUtilizationReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String binaId) throws IOException {
        return ResponseEntity.ok(analyticsService.getResourceUtilizationReport(from, to, binaId));
    }
}
