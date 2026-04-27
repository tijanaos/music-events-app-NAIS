package rs.ac.uns.acs.nais.GraphDatabaseService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ResourceType;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.IQueryService;

import java.util.List;

@RestController
@RequestMapping("/api/queries")
@RequiredArgsConstructor
@Tag(name = "Complex Queries", description = "Advanced graph queries with aggregation")
public class QueryController {

    private final IQueryService queryService;

    @GetMapping("/stage-resources")
    @Operation(summary = "Query 1: Active stages with total resource count and available quantity")
    public ResponseEntity<List<StageResourceSummary>> getStageResourceSummary() {
        return ResponseEntity.ok(queryService.getStageResourceSummary());
    }

    @GetMapping("/performer-bookings")
    @Operation(summary = "Query 2: Performers with approved reservation count and average fee")
    public ResponseEntity<List<PerformerBookingStats>> getPerformerBookingStats() {
        return ResponseEntity.ok(queryService.getPerformerBookingStats());
    }

    @GetMapping("/genre-stats")
    @Operation(summary = "Query 3: Reservation count, average fee and average popularity grouped by genre")
    public ResponseEntity<List<GenreReservationStats>> getGenreReservationStats() {
        return ResponseEntity.ok(queryService.getGenreReservationStats());
    }

    @GetMapping("/missing-resources")
    @Operation(summary = "Query 4: Per reservation - count of missing resources and total requested quantity aggregated")
    public ResponseEntity<List<ReservationMissingResource>> getMissingResources() {
        return ResponseEntity.ok(queryService.getReservationsWithMissingResources());
    }

    @GetMapping("/stage-available-resources")
    @Operation(summary = "Query 5: Per stage - aggregated count and total available quantity of a given resource type")
    public ResponseEntity<List<StageAvailableResource>> getStagesWithAvailableResource(
            @RequestParam Integer minQuantity,
            @RequestParam ResourceType resourceType) {
        return ResponseEntity.ok(queryService.getStagesWithAvailableResource(minQuantity, resourceType));
    }
}
