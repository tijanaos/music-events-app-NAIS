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
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ResourceUsageResponse;
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

    // Upit 1 (TEXT SEARCH):
    // Pretraga zahteva za rezervaciju po imenu/prezimenu izvodjaca ili napomeni,
    // uz opcioni filter po statusu i zanru, sortiranje po popularnosti,
    // i agregaciju koja broji zahteve po statusu.
    public ReservationSearchQueryResponse searchReservationsByPerformerText(
            String searchText, String status, String zanr) throws IOException {

        SearchResponse<ReservationRequestDocument> response = elasticsearchClient.search(search -> search
                        .index(RESERVATION_REQUESTS_INDEX)
                        .size(50)
                        .query(query -> query.bool(bool -> {
                            bool.must(Query.of(inner -> inner.bool(innerBool -> innerBool
                                    .should(matchQuery("ime_izvodjaca", searchText))
                                    .should(matchQuery("prezime_izvodjaca", searchText))
                                    .should(matchQuery("napomena", searchText))
                                    .minimumShouldMatch("1"))));
                            if (status != null && !status.isBlank()) {
                                bool.filter(termQuery("status_zahteva", status));
                            }
                            if (zanr != null && !zanr.isBlank()) {
                                bool.filter(termQuery("zanr", zanr));
                            }
                            return bool;
                        }))
                        .sort(sort -> sort.field(field -> field
                                .field("popularnost")
                                .order(SortOrder.Desc)))
                        .aggregations("by_status", agg -> agg
                                .terms(terms -> terms.field("status_zahteva"))),
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
    // Najkorisceniji resursi po bini — terms agregacija po nazivu bine,
    // sa sub-agregacijom koja broji top 5 resursa po bini i sumira dodeljenu kolicinu.
    public List<ResourceUsageByStageResponse> getMostUsedResourcesByStage() throws IOException {

        SearchResponse<ResourceUsageDocument> response = elasticsearchClient.search(search -> search
                        .index(RESOURCE_USAGE_INDEX)
                        .size(0)
                        .aggregations("by_stage", agg -> agg
                                .terms(terms -> terms.field("naziv_bine").size(20))
                                .aggregations("top_resources", sub -> sub
                                        .terms(terms -> terms.field("naziv_resursa").size(5))
                                        .aggregations("total_kolicina", kol -> kol
                                                .sum(sum -> sum.field("dodeljena_kolicina"))))),
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
                                    Double totalKol = null;
                                    if (resBucket.aggregations() != null && resBucket.aggregations().containsKey("total_kolicina")) {
                                        double val = resBucket.aggregations().get("total_kolicina").sum().value();
                                        totalKol = Double.isNaN(val) ? null : val;
                                    }
                                    return AggregationBucketResponse.builder()
                                            .key(resBucket.key().stringValue())
                                            .count(resBucket.docCount())
                                            .numericValue(totalKol)
                                            .build();
                                })
                                .toList();
                    }
                    return ResourceUsageByStageResponse.builder()
                            .nazivBine(stageBucket.key().stringValue())
                            .ukupnoKoriscenja(stageBucket.docCount())
                            .najkoriscenijiResursi(resources)
                            .build();
                })
                .toList();
    }

    // Upit 3:
    // Termini sa najvecim brojem resursa — date histogram po datumu koriscenja
    // u zadatom periodu, sa sum agregacijom dodeljene kolicine,
    // sortirani opadajuce po ukupnoj kolicini resursa.
    public List<AggregationBucketResponse> getTimeSlotsWithMostResources(
            LocalDate from, LocalDate to) throws IOException {

        SearchResponse<ResourceUsageDocument> response = elasticsearchClient.search(search -> search
                        .index(RESOURCE_USAGE_INDEX)
                        .size(0)
                        .query(query -> query.range(range -> range
                                .field("datum")
                                .gte(JsonData.of(from.toString()))
                                .lte(JsonData.of(to.toString()))))
                        .aggregations("by_date", agg -> agg
                                .dateHistogram(dh -> dh
                                        .field("datum")
                                        .calendarInterval(CalendarInterval.Day))
                                .aggregations("total_kolicina", sub -> sub
                                        .sum(sum -> sum.field("dodeljena_kolicina")))),
                ResourceUsageDocument.class);

        if (response.aggregations() == null || !response.aggregations().containsKey("by_date")) {
            return Collections.emptyList();
        }

        return response.aggregations().get("by_date").dateHistogram().buckets().array().stream()
                .filter(bucket -> bucket.docCount() > 0)
                .map(bucket -> {
                    double sumVal = bucket.aggregations() != null && bucket.aggregations().containsKey("total_kolicina")
                            ? bucket.aggregations().get("total_kolicina").sum().value() : 0.0;
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
    // Rezervacije koje su zahtevale resurse koji ne postoje u sistemu (ima taskove),
    // grupisane po bini sa prosecnim brojem taskova po bini,
    // sortirane opadajuce po broju takvih rezervacija.
    public List<AggregationBucketResponse> getReservationsWithMissingResourcesByStage() throws IOException {

        SearchResponse<ReservationRequestDocument> response = elasticsearchClient.search(search -> search
                        .index(RESERVATION_REQUESTS_INDEX)
                        .size(0)
                        .query(query -> query.bool(bool -> bool
                                .filter(termQuery("ima_taskove", true))))
                        .aggregations("by_stage", agg -> agg
                                .terms(terms -> terms
                                        .field("naziv_bine")
                                        .size(20)
                                        .order(NamedValue.of("_count", SortOrder.Desc)))
                                .aggregations("avg_taskova", sub -> sub
                                        .avg(avg -> avg.field("broj_taskova")))),
                ReservationRequestDocument.class);

        if (response.aggregations() == null || !response.aggregations().containsKey("by_stage")) {
            return Collections.emptyList();
        }

        return response.aggregations().get("by_stage").sterms().buckets().array().stream()
                .map(bucket -> {
                    Double avgTaskova = null;
                    if (bucket.aggregations() != null && bucket.aggregations().containsKey("avg_taskova")) {
                        double val = bucket.aggregations().get("avg_taskova").avg().value();
                        avgTaskova = Double.isNaN(val) ? null : val;
                    }
                    return AggregationBucketResponse.builder()
                            .key(bucket.key().stringValue())
                            .count(bucket.docCount())
                            .numericValue(avgTaskova)
                            .build();
                })
                .toList();
    }

    // Upit 5:
    // Izvestaj o iskorisenosti resursa za zadati vremenski period i opcionalnu binu.
    // Kombinuje dva upita: resource-usage (top resursi po frekvenciji i kolicini,
    // date histogram zauzetosti) i reservation-requests (broj rezervacija sa taskovima u periodu).
    public ResourceUtilizationReportResponse getResourceUtilizationReport(
            LocalDate from, LocalDate to, String binaId) throws IOException {

        SearchResponse<ResourceUsageDocument> usageResponse = elasticsearchClient.search(search -> search
                        .index(RESOURCE_USAGE_INDEX)
                        .size(0)
                        .query(query -> query.bool(bool -> {
                            bool.filter(dateRangeQuery("datum", from, to));
                            if (binaId != null && !binaId.isBlank()) {
                                bool.filter(termQuery("bina_id", binaId));
                            }
                            return bool;
                        }))
                        .aggregations("by_resource", agg -> agg
                                .terms(terms -> terms.field("naziv_resursa").size(10))
                                .aggregations("total_kolicina", sub -> sub
                                        .sum(sum -> sum.field("dodeljena_kolicina"))))
                        .aggregations("by_date", agg -> agg
                                .dateHistogram(dh -> dh
                                        .field("datum")
                                        .calendarInterval(CalendarInterval.Day))
                                .aggregations("total_kolicina", sub -> sub
                                        .sum(sum -> sum.field("dodeljena_kolicina")))),
                ResourceUsageDocument.class);

        SearchResponse<ReservationRequestDocument> reservationResponse = elasticsearchClient.search(search -> search
                        .index(RESERVATION_REQUESTS_INDEX)
                        .size(0)
                        .query(query -> query.bool(bool -> bool
                                .filter(termQuery("ima_taskove", true))
                                .filter(dateRangeQuery("datum_nastupa", from, to)))),
                ReservationRequestDocument.class);

        List<AggregationBucketResponse> resursiPoFrekvenciji = Collections.emptyList();
        List<AggregationBucketResponse> terminiBrojResursa = Collections.emptyList();

        if (usageResponse.aggregations() != null) {
            if (usageResponse.aggregations().containsKey("by_resource")) {
                resursiPoFrekvenciji = usageResponse.aggregations().get("by_resource").sterms().buckets().array().stream()
                        .map(bucket -> {
                            double sumVal = bucket.aggregations() != null && bucket.aggregations().containsKey("total_kolicina")
                                    ? bucket.aggregations().get("total_kolicina").sum().value() : 0.0;
                            return AggregationBucketResponse.builder()
                                    .key(bucket.key().stringValue())
                                    .count(bucket.docCount())
                                    .numericValue(Double.isNaN(sumVal) ? null : sumVal)
                                    .build();
                        })
                        .toList();
            }
            if (usageResponse.aggregations().containsKey("by_date")) {
                terminiBrojResursa = usageResponse.aggregations().get("by_date").dateHistogram().buckets().array().stream()
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
                .resursiPoFrekvenciji(resursiPoFrekvenciji)
                .terminiBrojResursa(terminiBrojResursa)
                .rezervacijeSaTaskovima(totalHits(reservationResponse))
                .build();
    }

    // --- Helper metode ---

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
                        .statusZahteva(doc.getStatusZahteva())
                        .datumSlanja(doc.getDatumSlanja())
                        .datumAzuriranja(doc.getDatumAzuriranja())
                        .napomena(doc.getNapomena())
                        .binaId(doc.getBinaId())
                        .nazivBine(doc.getNazivBine())
                        .tipBine(doc.getTipBine())
                        .kapacitetBine(doc.getKapacitetBine())
                        .izvodjacId(doc.getIzvodjacId())
                        .imeIzvodjaca(doc.getImeIzvodjaca())
                        .prezimeIzvodjaca(doc.getPrezimeIzvodjaca())
                        .zanr(doc.getZanr())
                        .popularnost(doc.getPopularnost())
                        .datumNastupa(doc.getDatumNastupa())
                        .vremePocetka(doc.getVremePocetka())
                        .vremeKraja(doc.getVremeKraja())
                        .zahtevanihResursa(doc.getZahtevanihResursa())
                        .imaTaskove(doc.getImaTaskove())
                        .brojTaskova(doc.getBrojTaskova())
                        .detaljiNastupa(doc.getDetaljiNastupa())
                        .build())
                .toList();
    }
}
