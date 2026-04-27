package rs.ac.uns.acs.nais.GraphDatabaseService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.GenreReservationStats;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.PerformerBookingStats;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.ResourceApprovalResult;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.StageConfirmationResult;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.StageResourceSummary;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.ReservationRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.StageRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.IQueryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryService implements IQueryService {

    private final StageRepository stageRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public List<StageResourceSummary> getStageResourceSummary() {
        return stageRepository.findStageResourceSummary();
    }

    @Override
    public List<PerformerBookingStats> getPerformerBookingStats() {
        return reservationRepository.findPerformerBookingStats();
    }

    @Override
    public List<GenreReservationStats> getGenreReservationStats() {
        return reservationRepository.findGenreReservationStats();
    }

    @Override
    public List<StageConfirmationResult> confirmStageForApprovedReservations() {
        return reservationRepository.confirmStageForApprovedReservations();
    }

    @Override
    public List<ResourceApprovalResult> approveExistingResourceRequests() {
        return reservationRepository.approveExistingResourceRequests();
    }
}
