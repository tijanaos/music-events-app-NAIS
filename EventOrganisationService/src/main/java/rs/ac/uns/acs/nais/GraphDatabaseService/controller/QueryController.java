package rs.ac.uns.acs.nais.GraphDatabaseService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.GenreReservationStats;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.PerformerBookingStats;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.ResourceApprovalResult;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.StageConfirmationResult;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.StageResourceSummary;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.QueryRepository;

import java.util.List;

@RestController
@RequestMapping("/api/queries")
@RequiredArgsConstructor
@Tag(name = "Complex Queries", description = "Advanced graph queries with aggregation")
public class QueryController {

    private final QueryRepository queryRepository;

    @GetMapping("/stage-resources")
    @Operation(summary = "Query 1: Active stages with total resource count and available quantity")
    public ResponseEntity<List<StageResourceSummary>> getStageResourceSummary() {
        return ResponseEntity.ok(queryRepository.getStageResourceSummary());
    }

    @GetMapping("/performer-bookings")
    @Operation(summary = "Query 2: Performers with approved reservation count and average fee")
    public ResponseEntity<List<PerformerBookingStats>> getPerformerBookingStats() {
        return ResponseEntity.ok(queryRepository.getPerformerBookingStats());
    }

    @GetMapping("/genre-stats")
    @Operation(summary = "Query 3: Reservation count, average fee and average popularity grouped by genre")
    public ResponseEntity<List<GenreReservationStats>> getGenreReservationStats() {
        return ResponseEntity.ok(queryRepository.getGenreReservationStats());
    }

    @PatchMapping("/confirm-stages")
    @Operation(summary = "Query 4 (CRUD): Set ON_STAGE.confirmed = true for all APPROVED reservations where stage is unconfirmed")
    public ResponseEntity<List<StageConfirmationResult>> confirmStages() {
        return ResponseEntity.ok(queryRepository.confirmStageForApprovedReservations());
    }

    @PatchMapping("/approve-resources")
    @Operation(summary = "Query 5 (CRUD): Set REQUIRES_RESOURCE.status = APPROVED for existing resources on APPROVED reservations")
    public ResponseEntity<List<ResourceApprovalResult>> approveResources() {
        return ResponseEntity.ok(queryRepository.approveExistingResourceRequests());
    }
}
