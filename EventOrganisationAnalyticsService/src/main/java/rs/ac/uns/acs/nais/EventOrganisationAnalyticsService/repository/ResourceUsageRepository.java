package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ResourceUsageDocument;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ResourceUsageRepository extends ElasticsearchRepository<ResourceUsageDocument, String> {

    List<ResourceUsageDocument> findByStageId(String stageId);

    List<ResourceUsageDocument> findByResourceId(String resourceId);

    List<ResourceUsageDocument> findByResourceType(String resourceType);

    List<ResourceUsageDocument> findByDateBetween(LocalDate from, LocalDate to);

    List<ResourceUsageDocument> findByBorrowedFromStageTrue();

    List<ResourceUsageDocument> findByReservationId(String reservationId);
}
