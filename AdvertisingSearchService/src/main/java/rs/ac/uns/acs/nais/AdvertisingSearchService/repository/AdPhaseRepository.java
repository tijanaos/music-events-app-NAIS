package rs.ac.uns.acs.nais.AdvertisingSearchService.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import rs.ac.uns.acs.nais.AdvertisingSearchService.model.AdPhaseDocument;

import java.util.List;

public interface AdPhaseRepository extends ElasticsearchRepository<AdPhaseDocument, Long> {

    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "bool": {
                      "should": [
                        { "term": { "ad_type_id": ?0 } },
                        { "term": { "ad_type_id": ?1 } },
                        { "term": { "ad_type_id": ?2 } },
                        { "term": { "ad_type_id": ?3 } },
                        { "term": { "ad_type_id": ?4 } },
                        { "term": { "ad_type_id": ?5 } },
                        { "term": { "ad_type_id": ?6 } },
                        { "term": { "ad_type_id": ?7 } },
                        { "term": { "ad_type_id": ?8 } },
                        { "term": { "ad_type_id": ?9 } }
                      ],
                      "minimum_should_match": 1
                    }
                  }
                ],
                "filter": [
                  { "term": { "is_active": true } },
                  { "term": { "requires_email_notification": true } },
                  { "term": { "is_final_phase": false } },
                  {
                    "range": {
                      "expected_duration_hours": {
                        "gte": ?10
                      }
                    }
                  }
                ]
              }
            }
            """)
    List<AdPhaseDocument> findNotificationPhases(
            Long adTypeId1,
            Long adTypeId2,
            Long adTypeId3,
            Long adTypeId4,
            Long adTypeId5,
            Long adTypeId6,
            Long adTypeId7,
            Long adTypeId8,
            Long adTypeId9,
            Long adTypeId10,
            int minimumDurationHours,
            Pageable pageable
    );

    @Query("""
            {
              "bool": {
                "should": [
                  { "match": { "phase_name": "?0" } },
                  { "match": { "description": "?0" } }
                ],
                "minimum_should_match": 1
              }
            }
            """)
    List<AdPhaseDocument> searchWorkflowText(String text, Pageable pageable);

    @Query("""
            {
              "bool": {
                "should": [
                  { "match": { "phase_name": "?0" } },
                  { "match": { "description": "?0" } }
                ],
                "minimum_should_match": 1,
                "filter": [
                  { "term": { "is_active": true } }
                ]
              }
            }
            """)
    List<AdPhaseDocument> searchActiveWorkflowText(String text, Pageable pageable);
}
