package rs.ac.uns.acs.nais.GraphDatabaseService.service;

import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ResourceType;

import java.util.List;

public interface IQueryService {

    List<StageResourceSummary> getStageResourceSummary();

    List<PerformerBookingStats> getPerformerBookingStats();

    List<GenreReservationStats> getGenreReservationStats();

    List<ReservationMissingResource> getReservationsWithMissingResources();

    List<StageAvailableResource> getStagesWithAvailableResource(Integer minQuantity, ResourceType resourceType);
}
