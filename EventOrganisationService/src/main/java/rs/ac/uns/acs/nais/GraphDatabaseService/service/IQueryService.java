package rs.ac.uns.acs.nais.GraphDatabaseService.service;

import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.GenreReservationStats;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.PerformerBookingStats;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.ResourceApprovalResult;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.StageConfirmationResult;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.StageResourceSummary;

import java.util.List;

public interface IQueryService {

    List<StageResourceSummary> getStageResourceSummary();

    List<PerformerBookingStats> getPerformerBookingStats();

    List<GenreReservationStats> getGenreReservationStats();

    List<StageConfirmationResult> confirmStageForApprovedReservations();

    List<ResourceApprovalResult> approveExistingResourceRequests();
}
