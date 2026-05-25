package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
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
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ResourceUtilizationReportResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ReservationRequestDocument;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ResourceUsageDocument;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    // sa ugnjezdenim agregacijama koja broji top 5 resursa po bini i sumira dodeljenu kolicinu
    public List<ResourceUsageByStageResponse> getMostUsedResourcesByStage() throws IOException {

        SearchResponse<ResourceUsageDocument> response = elasticsearchClient.search(search -> search
                        .index(RESOURCE_USAGE_INDEX)
                        .size(0)
                        .aggregations("by_stage", agg -> agg
                                .terms(terms -> terms.field("stage_name").size(20))
                                .aggregations("top_resources", sub -> sub
                                        .terms(terms -> terms.field("resource_name").size(5))
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
                                .toList();
                    }
                    return ResourceUsageByStageResponse.builder()
                            .stageName(stageBucket.key().stringValue())
                            .totalUsageCount(stageBucket.docCount())
                            .mostUsedResources(resources)
                            .build();
                })
                .toList();
    }

    // Upit 3:
    // Termini sa najvecim brojem resursa - date histogram po datumu koriscenja
    // u zadatom periodu, sa sum agregacijom dodeljene kolicine,
    // sortirani opadajuce po ukupnoj kolicini resursa
    public List<AggregationBucketResponse> getTimeSlotsWithMostResources(
            LocalDate from, LocalDate to) throws IOException {

        SearchResponse<ResourceUsageDocument> response = elasticsearchClient.search(search -> search
                        .index(RESOURCE_USAGE_INDEX)
                        .size(0)
                        .query(dateRangeQuery("date", from, to))
                        .aggregations("by_date", agg -> agg
                                .dateHistogram(dh -> dh
                                        .field("date")
                                        .calendarInterval(CalendarInterval.Day))
                                .aggregations("total_quantity", sub -> sub
                                        .sum(sum -> sum.field("allocated_quantity")))),
                ResourceUsageDocument.class);

        if (response.aggregations() == null || !response.aggregations().containsKey("by_date")) {
            return Collections.emptyList();
        }

        return response.aggregations().get("by_date").dateHistogram().buckets().array().stream()
                .filter(bucket -> bucket.docCount() > 0)
                .map(bucket -> {
                    double sumVal = bucket.aggregations() != null && bucket.aggregations().containsKey("total_quantity")
                            ? bucket.aggregations().get("total_quantity").sum().value() : 0.0;
                    return AggregationBucketResponse.builder()
                            .key(bucket.keyAsString())
                            .count(bucket.docCount())
                            .numericValue(Double.isNaN(sumVal) ? null : sumVal)
                            .build();
                })
                .sorted(Comparator.comparingDouble(
                        (AggregationBucketResponse b) -> b.getNumericValue() == null ? 0.0 : b.getNumericValue()
                ).reversed())
                .toList();
    }

    // Upit 4:
    // Rezervacije koje su zahtevale resurse koji ne postoje u sistemu (kreirani su taskovi),
    // grupisane po bini sa prosecnim brojem taskova po bini,
    // sortirane opadajuce po broju takvih rezervacija
    public List<AggregationBucketResponse> getReservationsWithMissingResourcesByStage() throws IOException {

        SearchResponse<ReservationRequestDocument> response = elasticsearchClient.search(search -> search
                        .index(RESERVATION_REQUESTS_INDEX)
                        .size(0)
                        .query(query -> query.bool(bool -> bool
                                .filter(termQuery("has_tasks", true))))
                        .aggregations("by_stage", agg -> agg
                                .terms(terms -> terms
                                        .field("stage_name")
                                        .size(20)
                                        .order(NamedValue.of("_count", SortOrder.Desc)))
                                .aggregations("avg_tasks", sub -> sub
                                        .avg(avg -> avg.field("task_count")))),
                ReservationRequestDocument.class);

        if (response.aggregations() == null || !response.aggregations().containsKey("by_stage")) {
            return Collections.emptyList();
        }

        return response.aggregations().get("by_stage").sterms().buckets().array().stream()
                .map(bucket -> {
                    Double avgTasks = null;
                    if (bucket.aggregations() != null && bucket.aggregations().containsKey("avg_tasks")) {
                        double val = bucket.aggregations().get("avg_tasks").avg().value();
                        avgTasks = Double.isNaN(val) ? null : val;
                    }
                    return AggregationBucketResponse.builder()
                            .key(bucket.key().stringValue())
                            .count(bucket.docCount())
                            .numericValue(avgTasks)
                            .build();
                })
                .toList();
    }

    // Upit 5:
    // Izvestaj o iskorisenosti resursa za zadati vremenski period i opcionalnu binu.
    // Kombinuje dva upita: resource-usage (top resursi po pojavljivanju i kolicini,
    // date histogram zauzetosti) i reservation-requests (broj rezervacija sa taskovima u periodu).
    public ResourceUtilizationReportResponse getResourceUtilizationReport(
            LocalDate from, LocalDate to, String stageId) throws IOException {

        SearchResponse<ResourceUsageDocument> usageResponse = elasticsearchClient.search(search -> search
                        .index(RESOURCE_USAGE_INDEX)
                        .size(0)
                        .query(query -> query.bool(bool -> {
                            bool.filter(dateRangeQuery("date", from, to));
                            if (stageId != null && !stageId.isBlank()) {
                                bool.filter(termQuery("stage_id", stageId));
                            }
                            return bool;
                        }))
                        .aggregations("by_resource", agg -> agg
                                .terms(terms -> terms.field("resource_name").size(10))
                                .aggregations("total_quantity", sub -> sub
                                        .sum(sum -> sum.field("allocated_quantity"))))
                        .aggregations("by_date", agg -> agg
                                .dateHistogram(dh -> dh
                                        .field("date")
                                        .calendarInterval(CalendarInterval.Day))
                                .aggregations("total_quantity", sub -> sub
                                        .sum(sum -> sum.field("allocated_quantity")))),
                ResourceUsageDocument.class);

        SearchResponse<ReservationRequestDocument> reservationResponse = elasticsearchClient.search(search -> search
                        .index(RESERVATION_REQUESTS_INDEX)
                        .size(0)
                        .query(query -> query.bool(bool -> {
                            bool.filter(termQuery("has_tasks", true));
                            bool.filter(dateRangeQuery("performance_date", from, to));
                            if (stageId != null && !stageId.isBlank()) {
                                bool.filter(termQuery("stage_id", stageId));
                            }
                            return bool;
                        })),
                ReservationRequestDocument.class);

        List<AggregationBucketResponse> resourcesByFrequency = Collections.emptyList();
        List<AggregationBucketResponse> timeSlotsResourceCount = Collections.emptyList();

        if (usageResponse.aggregations() != null) {
            if (usageResponse.aggregations().containsKey("by_resource")) {
                resourcesByFrequency = usageResponse.aggregations().get("by_resource").sterms().buckets().array().stream()
                        .map(bucket -> {
                            double sumVal = bucket.aggregations() != null && bucket.aggregations().containsKey("total_quantity")
                                    ? bucket.aggregations().get("total_quantity").sum().value() : 0.0;
                            return AggregationBucketResponse.builder()
                                    .key(bucket.key().stringValue())
                                    .count(bucket.docCount())
                                    .numericValue(Double.isNaN(sumVal) ? null : sumVal)
                                    .build();
                        })
                        .toList();
            }
            if (usageResponse.aggregations().containsKey("by_date")) {
                timeSlotsResourceCount = usageResponse.aggregations().get("by_date").dateHistogram().buckets().array().stream()
                        .filter(bucket -> bucket.docCount() > 0)
                        .map(bucket -> AggregationBucketResponse.builder()
                                .key(bucket.keyAsString())
                                .count(bucket.docCount())
                                .build())
                        .toList();
            }
        }

        return ResourceUtilizationReportResponse.builder()
                .totalHits(totalHits(usageResponse))
                .resourcesByFrequency(resourcesByFrequency)
                .timeSlotsResourceCount(timeSlotsResourceCount)
                .reservationsWithTasks(totalHits(reservationResponse))
                .build();
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

    private Query termQuery(String field, boolean value) {
        return Query.of(query -> query.term(term -> term.field(field).value(value)));
    }

    private Query dateRangeQuery(String field, LocalDate from, LocalDate to) {
        return Query.of(query -> query.range(range -> range
                .field(field)
                .gte(JsonData.of(from.toString()))
                .lte(JsonData.of(to.toString()))));
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
