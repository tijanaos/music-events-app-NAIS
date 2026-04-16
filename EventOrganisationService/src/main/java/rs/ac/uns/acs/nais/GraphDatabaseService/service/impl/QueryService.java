package rs.ac.uns.acs.nais.GraphDatabaseService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ResourceType;
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
    public List<ReservationMissingResource> getReservationsWithMissingResources() {
        return reservationRepository.findReservationsWithMissingResources();
    }

    @Override
    public List<StageAvailableResource> getStagesWithAvailableResource(Integer minQuantity, ResourceType resourceType) {
        return stageRepository.findStagesWithAvailableResource(minQuantity, resourceType.name());
    }
}
