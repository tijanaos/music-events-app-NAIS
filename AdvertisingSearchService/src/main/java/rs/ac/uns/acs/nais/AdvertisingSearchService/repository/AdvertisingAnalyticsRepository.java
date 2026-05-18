package rs.ac.uns.acs.nais.AdvertisingSearchService.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdPhaseResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdPhaseSearchQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdTypeResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdTypeSearchQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AggregationBucketResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.ApprovalSummaryQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.model.AdPhaseDocument;
import rs.ac.uns.acs.nais.AdvertisingSearchService.model.AdTypeDocument;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class AdvertisingAnalyticsRepository {

    private static final String AD_TYPES_INDEX = "ad-types";
    private static final String AD_PHASES_INDEX = "ad-phases";

    private final ElasticsearchClient elasticsearchClient;

    public AdTypeSearchQueryResponse searchActiveAdTypes(String text, String category, String contentType) throws IOException {
        SearchResponse<AdTypeDocument> response = elasticsearchClient.search(search -> search
                        .index(AD_TYPES_INDEX)
                        .size(50)
                        .query(query -> query.bool(bool -> bool
                                .must(Query.of(innerQuery -> innerQuery.bool(innerBool -> innerBool
                                        .should(matchQuery("name", text))
                                        .should(matchQuery("description", text))
                                        .minimumShouldMatch("1"))))
                                .filter(termQuery("is_active", true))
                                .filter(termQuery("category", category))
                                .filter(termQuery("content_type", contentType))
                        ))
                        .sort(sort -> sort.field(field -> field
                                .field("average_duration_days")
                                .order(SortOrder.Asc)))
                        .aggregations("by_target_channel", aggregation -> aggregation
                                .terms(terms -> terms.field("target_channel"))
                                .aggregations("avg_duration_days", sub -> sub
                                        .avg(avg -> avg.field("average_duration_days")))),
                AdTypeDocument.class);

        return AdTypeSearchQueryResponse.builder()
                .totalHits(totalHits(response))
                .results(mapAdTypeHits(response.hits().hits()))
                .groupedByTargetChannel(extractBuckets(response.aggregations(), "by_target_channel", "avg_duration_days"))
                .build();
    }

    public AdPhaseSearchQueryResponse findNotificationPhases(String adTypeName, int minimumDurationHours) throws IOException {
        SearchResponse<AdTypeDocument> adTypeResponse = elasticsearchClient.search(search -> search
                        .index(AD_TYPES_INDEX)
                        .size(20)
                        .query(query -> query.match(match -> match
                                .field("name")
                                .query(adTypeName))),
                AdTypeDocument.class);

        List<Long> adTypeIds = adTypeResponse.hits().hits().stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .map(AdTypeDocument::getId)
                .toList();

        if (adTypeIds.isEmpty()) {
            return AdPhaseSearchQueryResponse.builder()
                    .totalHits(0)
                    .results(Collections.emptyList())
                    .groupedByResponsibleRole(Collections.emptyList())
                    .build();
        }

        SearchResponse<AdPhaseDocument> response = elasticsearchClient.search(search -> search
                        .index(AD_PHASES_INDEX)
                        .size(50)
                        .query(query -> query.bool(bool -> bool
                                .must(adTypeIdsShouldQuery(adTypeIds))
                                .filter(termQuery("is_active", true))
                                .filter(termQuery("requires_email_notification", true))
                                .filter(termQuery("is_final_phase", false))
                                .filter(rangeQuery("expected_duration_hours", minimumDurationHours))
                        ))
                        .sort(sort -> sort.field(field -> field
                                .field("phase_order")
                                .order(SortOrder.Asc)))
                        .aggregations("by_responsible_role", aggregation -> aggregation
                                .terms(terms -> terms.field("responsible_role"))
                                .aggregations("avg_duration_hours", sub -> sub
                                        .avg(avg -> avg.field("expected_duration_hours")))),
                AdPhaseDocument.class);

        return AdPhaseSearchQueryResponse.builder()
                .totalHits(totalHits(response))
                .results(mapAdPhaseHits(response.hits().hits()))
                .groupedByResponsibleRole(extractBuckets(response.aggregations(), "by_responsible_role", "avg_duration_hours"))
                .build();
    }

    public AdPhaseSearchQueryResponse searchPhaseWorkflowText(String text, boolean activeOnly) throws IOException {
        SearchResponse<AdPhaseDocument> response = elasticsearchClient.search(search -> search
                        .index(AD_PHASES_INDEX)
                        .size(50)
                        .query(query -> query.bool(bool -> {
                            bool.should(matchQuery("phase_name", text));
                            bool.should(matchQuery("description", text));
                            bool.minimumShouldMatch("1");
                            if (activeOnly) {
                                bool.filter(termQuery("is_active", true));
                            }
                            return bool;
                        }))
                        .sort(sort -> sort.score(score -> score.order(SortOrder.Desc)))
                        .sort(sort -> sort.field(field -> field
                                .field("expected_duration_hours")
                                .order(SortOrder.Desc)))
                        .aggregations("by_responsible_role", aggregation -> aggregation
                                .terms(terms -> terms.field("responsible_role"))
                                .aggregations("avg_duration_hours", sub -> sub
                                        .avg(avg -> avg.field("expected_duration_hours")))),
                AdPhaseDocument.class);

        return AdPhaseSearchQueryResponse.builder()
                .totalHits(totalHits(response))
                .results(mapAdPhaseHits(response.hits().hits()))
                .groupedByResponsibleRole(extractBuckets(response.aggregations(), "by_responsible_role", "avg_duration_hours"))
                .build();
    }

    public ApprovalSummaryQueryResponse findApprovalHeavyAdTypes(List<String> categories,
                                                                 int minDurationDays,
                                                                 int maxDurationDays) throws IOException {
        SearchResponse<AdTypeDocument> response = elasticsearchClient.search(search -> search
                        .index(AD_TYPES_INDEX)
                        .size(50)
                        .query(query -> query.bool(bool -> bool
                                .must(categoryShouldQuery(categories))
                                .filter(termQuery("is_active", true))
                                .filter(termQuery("requires_approval", true))
                                .filter(rangeQuery("average_duration_days", minDurationDays, maxDurationDays))
                        ))
                        .sort(sort -> sort.field(field -> field
                                .field("average_duration_days")
                                .order(SortOrder.Desc)))
                        .aggregations("by_category", aggregation -> aggregation
                                .terms(terms -> terms.field("category"))
                                .aggregations("avg_duration_days", sub -> sub
                                        .avg(avg -> avg.field("average_duration_days")))),
                AdTypeDocument.class);

        return ApprovalSummaryQueryResponse.builder()
                .totalHits(totalHits(response))
                .results(mapAdTypeHits(response.hits().hits()))
                .groupedByCategory(extractBuckets(response.aggregations(), "by_category", "avg_duration_days"))
                .build();
    }

    private Query matchQuery(String field, String text) {
        return Query.of(query -> query.match(match -> match
                .field(field)
                .query(text)));
    }

    private Query termQuery(String field, String value) {
        return Query.of(query -> query.term(term -> term.field(field).value(value)));
    }

    private Query termQuery(String field, boolean value) {
        return Query.of(query -> query.term(term -> term.field(field).value(value)));
    }

    private Query termQuery(String field, Long value) {
        return Query.of(query -> query.term(term -> term.field(field).value(value)));
    }

    private Query rangeQuery(String field, int minimumValue) {
        return Query.of(query -> query.range(range -> range
                .field(field)
                .gte(co.elastic.clients.json.JsonData.of(minimumValue))));
    }

    private Query rangeQuery(String field, int minimumValue, int maximumValue) {
        return Query.of(query -> query.range(range -> range
                .field(field)
                .gte(co.elastic.clients.json.JsonData.of(minimumValue))
                .lte(co.elastic.clients.json.JsonData.of(maximumValue))));
    }

    private Query categoryShouldQuery(List<String> categories) {
        return Query.of(query -> query.bool(bool -> {
            categories.stream()
                    .filter(Objects::nonNull)
                    .forEach(category -> bool.should(termQuery("category", category)));
            bool.minimumShouldMatch("1");
            return bool;
        }));
    }

    private Query adTypeIdsShouldQuery(List<Long> adTypeIds) {
        return Query.of(query -> query.bool(bool -> {
            adTypeIds.stream()
                    .filter(Objects::nonNull)
                    .forEach(adTypeId -> bool.should(termQuery("ad_type_id", adTypeId)));
            bool.minimumShouldMatch("1");
            return bool;
        }));
    }

    private long totalHits(SearchResponse<?> response) {
        return response.hits().total() == null ? response.hits().hits().size() : response.hits().total().value();
    }

    private List<AdTypeResponse> mapAdTypeHits(List<Hit<AdTypeDocument>> hits) {
        return hits.stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .map(document -> AdTypeResponse.builder()
                        .id(document.getId())
                        .name(document.getName())
                        .description(document.getDescription())
                        .contentType(document.getContentType())
                        .category(document.getCategory())
                        .targetChannel(document.getTargetChannel())
                        .isActive(document.getIsActive())
                        .requiresApproval(document.getRequiresApproval())
                        .averageDurationDays(document.getAverageDurationDays())
                        .createdAt(document.getCreatedAt())
                        .build())
                .toList();
    }

    private List<AdPhaseResponse> mapAdPhaseHits(List<Hit<AdPhaseDocument>> hits) {
        return hits.stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .map(document -> AdPhaseResponse.builder()
                        .id(document.getId())
                        .adTypeId(document.getAdTypeId())
                        .phaseName(document.getPhaseName())
                        .description(document.getDescription())
                        .phaseOrder(document.getPhaseOrder())
                        .responsibleRole(document.getResponsibleRole())
                        .requiresEmailNotification(document.getRequiresEmailNotification())
                        .isFinalPhase(document.getIsFinalPhase())
                        .isActive(document.getIsActive())
                        .expectedDurationHours(document.getExpectedDurationHours())
                        .createdAt(document.getCreatedAt())
                        .build())
                .toList();
    }

    private List<AggregationBucketResponse> extractBuckets(Map<String, co.elastic.clients.elasticsearch._types.aggregations.Aggregate> aggregations,
                                                           String aggregationName,
                                                           String metricName) {
        if (aggregations == null || !aggregations.containsKey(aggregationName)) {
            return Collections.emptyList();
        }
        return aggregations.get(aggregationName).sterms().buckets().array().stream()
                .map(bucket -> mapBucket(bucket, metricName))
                .toList();
    }

    private AggregationBucketResponse mapBucket(StringTermsBucket bucket, String metricName) {
        Double avg = null;
        if (bucket.aggregations() != null && bucket.aggregations().containsKey(metricName)) {
            double value = bucket.aggregations().get(metricName).avg().value();
            avg = Double.isNaN(value) ? null : value;
        }
        return AggregationBucketResponse.builder()
                .groupValue(bucket.key().stringValue())
                .matchingDocumentsCount(bucket.docCount())
                .averageCalculatedValue(avg)
                .build();
    }
}
