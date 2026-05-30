package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ReservationRequestDocument;

import java.util.List;

@Repository
public interface ReservationRequestRepository extends ElasticsearchRepository<ReservationRequestDocument, String> {

    List<ReservationRequestDocument> findByStageId(String stageId);

    List<ReservationRequestDocument> findByRequestStatus(String requestStatus);

    List<ReservationRequestDocument> findByPerformerId(String performerId);

    List<ReservationRequestDocument> findByHasTasksTrue();
}
