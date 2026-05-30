package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.NamedValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.AggregationBucketResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ReservationRequestResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ReservationSearchQueryResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ResourceUsageByStageResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ReservationRequestDocument;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ResourceUsageDocument;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EventOrganisationAnalyticsRepository {

    private static final String RESERVATION_REQUESTS_INDEX = "reservation-requests";
    private static final String RESOURCE_USAGE_INDEX = "resource-usage";

    private final ElasticsearchClient elasticsearchClient;

    // Upit 1:
    // Pretraga zahteva za rezervaciju po imenu/prezimenu izvodjaca ili napomeni,
    // uz opcioni filter po statusu i zanru, sortiranje po popularnosti,
    // i agregaciju koja broji zahteve po statusu.
    public ReservationSearchQueryResponse searchReservationsByPerformerText(
            String searchText, String status, String genre) throws IOException {

        SearchResponse<ReservationRequestDocument> response = elasticsearchClient.search(search -> search
                        .index(RESERVATION_REQUESTS_INDEX)
                        .size(50)
                        .query(query -> query.bool(bool -> {
                            bool.must(Query.of(inner -> inner.bool(innerBool -> innerBool
                                    .should(matchQuery("performer_first_name", searchText))
                                    .should(matchQuery("performer_last_name", searchText))
                                    .should(matchQuery("note", searchText))
                                    .minimumShouldMatch("1"))));
                            if (status != null && !status.isBlank()) {
                                bool.filter(termQuery("request_status", status));
                            }
                            if (genre != null && !genre.isBlank()) {
                                bool.filter(termQuery("genre", genre));
                            }
                            return bool;
                        }))
                        .sort(sort -> sort.field(field -> field
                                .field("popularity")
                                .order(SortOrder.Desc)))
                        .aggregations("by_status", agg -> agg
                                .terms(terms -> terms.field("request_status"))),
                ReservationRequestDocument.class);

        Map<String, Long> groupedByStatus = new HashMap<>();
        if (response.aggregations() != null && response.aggregations().containsKey("by_status")) {
            response.aggregations().get("by_status").sterms().buckets().array()
                    .forEach(bucket -> groupedByStatus.put(bucket.key().stringValue(), bucket.docCount()));
        }

        return ReservationSearchQueryResponse.builder()
                .totalHits(totalHits(response))
                .results(mapReservationHits(response.hits().hits()))
                .groupedByStatus(groupedByStatus)
                .build();
    }

    // Upit 2:
    // Najkorisceniji resursi po bini - terms agregacija po nazivu bine,
    // za zadati tip resursa koji je stvarno dodeljen, uz ugnjezdene agregacije
    // koje broje top 5 resursa po bini i sumiraju dodeljenu kolicinu.
    public List<ResourceUsageByStageResponse> getMostUsedResourcesByStage(String resourceType) throws IOException {

        SearchResponse<ResourceUsageDocument> response = elasticsearchClient.search(search -> search
                        .index(RESOURCE_USAGE_INDEX)
                        .size(0)
                        .query(query -> query.bool(bool -> bool
                                .filter(termQuery("resource_type", normalizeResourceType(resourceType)))
                                .filter(greaterThanQuery("allocated_quantity", 0))))
                        .aggregations("by_stage", agg -> agg
                                .terms(terms -> terms
                                        .field("stage_name")
                                        .size(20)
                                        .order(NamedValue.of("_count", SortOrder.Desc)))
                                .aggregations("top_resources", sub -> sub
                                        .terms(terms -> terms
                                                .field("resource_name")
                                                .size(5)
                                                .order(NamedValue.of("_count", SortOrder.Desc)))
                                        .aggregations("total_quantity", quantity -> quantity
                                                .sum(sum -> sum.field("allocated_quantity"))))),
                ResourceUsageDocument.class);

        if (response.aggregations() == null || !response.aggregations().containsKey("by_stage")) {
            return Collections.emptyList();
        }

        return response.aggregations().get("by_stage").sterms().buckets().array().stream()
                .map(stageBucket -> {
                    List<AggregationBucketResponse> resources = Collections.emptyList();
                    if (stageBucket.aggregations() != null && stageBucket.aggregations().containsKey("top_resources")) {
                        resources = stageBucket.aggregations().get("top_resources").sterms().buckets().array().stream()
                                .map(resBucket -> {
                                    Double totalQuantity = null;
                                    if (resBucket.aggregations() != null && resBucket.aggregations().containsKey("total_quantity")) {
                                        double val = resBucket.aggregations().get("total_quantity").sum().value();
                                        totalQuantity = Double.isNaN(val) ? null : val;
                                    }
                                    return AggregationBucketResponse.builder()
                                            .key(resBucket.key().stringValue())
                                            .count(resBucket.docCount())
                                            .numericValue(totalQuantity)
                                            .build();
                                })
                                .collect(Collectors.toCollection(ArrayList::new));
                    }
                    return ResourceUsageByStageResponse.builder()
                            .stageName(stageBucket.key().stringValue())
                            .totalUsageCount(stageBucket.docCount())
                            .mostUsedResources(resources)
                            .build();
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Upit 3:
    // Najoptereceniji sati za zadati tip resursa u zadatom periodu.
    // Filtrira stvarno dodeljene resurse, grupise ih po satu pocetka,
    // sabira dodeljenu kolicinu i sortira sate po ukupnom opterecenju.
    public List<AggregationBucketResponse> getPeakResourceHours(
            LocalDate from, LocalDate to, String resourceType) throws IOException {

        SearchResponse<ResourceUsageDocument> response = elasticsearchClient.search(search -> search
                        .index(RESOURCE_USAGE_INDEX)
                        .size(0)
                        .query(query -> query.bool(bool -> bool
                                .filter(dateRangeQuery("date", from, to))
                                .filter(termQuery("resource_type", normalizeResourceType(resourceType)))
                                .filter(greaterThanQuery("allocated_quantity", 0))))
                        .aggregations("by_start_hour", agg -> agg
                                .terms(terms -> terms
                                        .field("start_time")
                                        .size(24)
                                        .order(NamedValue.of("total_quantity", SortOrder.Desc)))
                                .aggregations("total_quantity", sub -> sub
                                        .sum(sum -> sum.field("allocated_quantity")))),
                ResourceUsageDocument.class);

        if (response.aggregations() == null || !response.aggregations().containsKey("by_start_hour")) {
            return Collections.emptyList();
        }

        return response.aggregations().get("by_start_hour").lterms().buckets().array().stream()
                .map(bucket -> {
                    double sumVal = bucket.aggregations() != null && bucket.aggregations().containsKey("total_quantity")
                            ? bucket.aggregations().get("total_quantity").sum().value() : 0.0;
                    return AggregationBucketResponse.builder()
                            .key(String.format("%02d:00", bucket.key()))
                            .count(bucket.docCount())
                            .numericValue(Double.isNaN(sumVal) ? null : sumVal)
                            .build();
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // --- Helper metode ---

    private Query matchQuery(String field, String text) {
        return Query.of(query -> query.match(match -> match
                .field(field)
                .query(text)
                .fuzziness("AUTO")));
    }

    private Query termQuery(String field, String value) {
        return Query.of(query -> query.term(term -> term.field(field).value(value)));
    }

    private Query greaterThanQuery(String field, int value) {
        return Query.of(query -> query.range(range -> range
                .field(field)
                .gt(JsonData.of(value))));
    }

    private Query dateRangeQuery(String field, LocalDate from, LocalDate to) {
        return Query.of(query -> query.range(range -> range
                .field(field)
                .gte(JsonData.of(from.toString()))
                .lte(JsonData.of(to.toString()))));
    }

    private String normalizeResourceType(String resourceType) {
        return resourceType == null ? "" : resourceType.trim().toUpperCase();
    }

    private long totalHits(SearchResponse<?> response) {
        return response.hits().total() == null
                ? response.hits().hits().size()
                : response.hits().total().value();
    }

    private List<ReservationRequestResponse> mapReservationHits(List<Hit<ReservationRequestDocument>> hits) {
        return hits.stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .map(doc -> ReservationRequestResponse.builder()
                        .id(doc.getId())
                        .requestStatus(doc.getRequestStatus())
                        .sentDate(doc.getSentDate())
                        .updatedDate(doc.getUpdatedDate())
                        .note(doc.getNote())
                        .stageId(doc.getStageId())
                        .stageName(doc.getStageName())
                        .stageType(doc.getStageType())
                        .stageCapacity(doc.getStageCapacity())
                        .performerId(doc.getPerformerId())
                        .performerFirstName(doc.getPerformerFirstName())
                        .performerLastName(doc.getPerformerLastName())
                        .genre(doc.getGenre())
                        .popularity(doc.getPopularity())
                        .performanceDate(doc.getPerformanceDate())
                        .startTime(doc.getStartTime())
                        .endTime(doc.getEndTime())
                        .requestedResources(doc.getRequestedResources())
                        .hasTasks(doc.getHasTasks())
                        .taskCount(doc.getTaskCount())
                        .performanceDetails(doc.getPerformanceDetails())
                        .build())
                .toList();
    }
}
