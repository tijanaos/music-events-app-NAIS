package rs.ac.uns.acs.nais.AdvertisingSearchService.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import rs.ac.uns.acs.nais.AdvertisingSearchService.model.AdTypeDocument;

import java.util.List;

public interface AdTypeRepository extends ElasticsearchRepository<AdTypeDocument, Long> {

    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "bool": {
                      "should": [
                        { "match": { "name": "?0" } },
                        { "match": { "description": "?0" } }
                      ],
                      "minimum_should_match": 1
                    }
                  }
                ],
                "filter": [
                  { "term": { "is_active": true } },
                  { "term": { "category": "?1" } },
                  { "term": { "content_type": "?2" } }
                ]
              }
            }
            """)
    List<AdTypeDocument> searchActiveAdTypes(String text, String category, String contentType, Pageable pageable);

    @Query("""
            {
              "query": {
                "bool": {
                  "must": [
                    {
                      "bool": {
                        "should": [
                          { "match": { "name": "?0" } },
                          { "match": { "description": "?0" } }
                        ],
                        "minimum_should_match": 1
                      }
                    }
                  ],
                  "filter": [
                    { "term": { "is_active": true } },
                    { "term": { "category": "?1" } },
                    { "term": { "content_type": "?2" } }
                  ]
                }
              },
              "aggs": {
                "by_target_channel": {
                  "terms": {
                    "field": "target_channel"
                  }
                }
              }
            }
            """)
    List<AdTypeDocument> searchActiveAdTypesWithAggregationDsl(String text, String category, String contentType, Pageable pageable);

    @Query("""
            {
              "match": {
                "name": "?0"
              }
            }
            """)
    List<AdTypeDocument> findByNameForAnalytics(String adTypeName, Pageable pageable);

    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "bool": {
                      "should": [
                        { "term": { "category": "?0" } },
                        { "term": { "category": "?1" } },
                        { "term": { "category": "?2" } }
                      ],
                      "minimum_should_match": 1
                    }
                  }
                ],
                "filter": [
                  { "term": { "is_active": true } },
                  { "term": { "requires_approval": true } },
                  {
                    "range": {
                      "average_duration_days": {
                        "gte": ?3,
                        "lte": ?4
                      }
                    }
                  }
                ]
              }
            }
            """)
    List<AdTypeDocument> findApprovalHeavyAdTypes(
            String firstCategory,
            String secondCategory,
            String thirdCategory,
            int minDurationDays,
            int maxDurationDays,
            Pageable pageable
    );
}
