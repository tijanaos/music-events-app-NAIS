package rs.ac.uns.acs.nais.AdvertisingSearchService.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdPhaseResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdPhaseSearchQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdTypeResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdTypeSearchQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AggregationBucketResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.ApprovalSummaryQueryResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.model.AdPhaseDocument;
import rs.ac.uns.acs.nais.AdvertisingSearchService.model.AdTypeDocument;

import java.util.Collections;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AdvertisingAnalyticsRepository {
    private static final int DEFAULT_LIMIT = 50;
    private static final int MAX_AD_TYPE_IDS = 10;

    private final AdTypeRepository adTypeRepository;
    private final AdPhaseRepository adPhaseRepository;

    public AdTypeSearchQueryResponse searchActiveAdTypes(String text, String category, String contentType) {
        List<AdTypeDocument> results = adTypeRepository.searchActiveAdTypes(
                text,
                category,
                contentType,
                PageRequest.of(0, DEFAULT_LIMIT, Sort.by(Sort.Direction.ASC, "averageDurationDays"))
        );

        return AdTypeSearchQueryResponse.builder()
                .totalHits(results.size())
                .results(mapAdTypeDocuments(results))
                .groupedByTargetChannel(groupAdTypesByTargetChannel(results))
                .build();
    }

    public AdPhaseSearchQueryResponse findNotificationPhases(String adTypeName, int minimumDurationHours) {
        List<Long> adTypeIds = adTypeRepository.findByNameForAnalytics(
                        adTypeName,
                        PageRequest.of(0, MAX_AD_TYPE_IDS)
                ).stream()
                .map(AdTypeDocument::getId)
                .filter(Objects::nonNull)
                .limit(MAX_AD_TYPE_IDS)
                .toList();

        if (adTypeIds.isEmpty()) {
            return AdPhaseSearchQueryResponse.builder()
                    .totalHits(0)
                    .results(Collections.emptyList())
                    .groupedByResponsibleRole(Collections.emptyList())
                    .build();
        }

        List<Long> paddedIds = padIds(adTypeIds, MAX_AD_TYPE_IDS);
        List<AdPhaseDocument> results = adPhaseRepository.findNotificationPhases(
                paddedIds.get(0),
                paddedIds.get(1),
                paddedIds.get(2),
                paddedIds.get(3),
                paddedIds.get(4),
                paddedIds.get(5),
                paddedIds.get(6),
                paddedIds.get(7),
                paddedIds.get(8),
                paddedIds.get(9),
                minimumDurationHours,
                PageRequest.of(0, DEFAULT_LIMIT, Sort.by(Sort.Direction.ASC, "phaseOrder"))
        );

        return AdPhaseSearchQueryResponse.builder()
                .totalHits(results.size())
                .results(mapAdPhaseDocuments(results))
                .groupedByResponsibleRole(groupAdPhasesByResponsibleRole(results))
                .build();
    }

    public AdPhaseSearchQueryResponse searchPhaseWorkflowText(String text, boolean activeOnly) {
        List<AdPhaseDocument> results = activeOnly
                ? adPhaseRepository.searchActiveWorkflowText(
                text,
                PageRequest.of(0, DEFAULT_LIMIT, Sort.by(Sort.Direction.DESC, "expectedDurationHours"))
        )
                : adPhaseRepository.searchWorkflowText(
                text,
                PageRequest.of(0, DEFAULT_LIMIT, Sort.by(Sort.Direction.DESC, "expectedDurationHours"))
        );

        return AdPhaseSearchQueryResponse.builder()
                .totalHits(results.size())
                .results(mapAdPhaseDocuments(results))
                .groupedByResponsibleRole(groupAdPhasesByResponsibleRole(results))
                .build();
    }

    public ApprovalSummaryQueryResponse findApprovalHeavyAdTypes(List<String> categories, int minDurationDays, int maxDurationDays) {
        List<String> sanitizedCategories = sanitizeCategories(categories);
        if (sanitizedCategories.isEmpty()) {
            return ApprovalSummaryQueryResponse.builder()
                    .totalHits(0)
                    .results(Collections.emptyList())
                    .groupedByCategory(Collections.emptyList())
                    .build();
        }

        List<AdTypeDocument> results = adTypeRepository.findApprovalHeavyAdTypes(
                valueAtOrPlaceholder(sanitizedCategories, 0),
                valueAtOrPlaceholder(sanitizedCategories, 1),
                valueAtOrPlaceholder(sanitizedCategories, 2),
                minDurationDays,
                maxDurationDays,
                PageRequest.of(0, DEFAULT_LIMIT, Sort.by(Sort.Direction.DESC, "averageDurationDays"))
        );

        return ApprovalSummaryQueryResponse.builder()
                .totalHits(results.size())
                .results(mapAdTypeDocuments(results))
                .groupedByCategory(groupAdTypesByCategory(results))
                .build();
    }

    private List<String> sanitizeCategories(List<String> categories) {
        return categories == null
                ? Collections.emptyList()
                : categories.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .limit(3)
                .toList();
    }

    private String valueAtOrPlaceholder(List<String> values, int index) {
        return index < values.size() ? values.get(index) : "__missing_value__";
    }

    private List<Long> padIds(List<Long> adTypeIds, int targetSize) {
        List<Long> padded = adTypeIds.stream().limit(targetSize).collect(Collectors.toList());
        while (padded.size() < targetSize) {
            padded.add(-1L);
        }
        return padded;
    }

    private List<AdTypeResponse> mapAdTypeDocuments(List<AdTypeDocument> documents) {
        return documents.stream()
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

    private List<AdPhaseResponse> mapAdPhaseDocuments(List<AdPhaseDocument> documents) {
        return documents.stream()
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

    private List<AggregationBucketResponse> groupAdTypesByTargetChannel(List<AdTypeDocument> documents) {
        return buildBuckets(
                documents,
                document -> document.getTargetChannel() == null ? "unknown" : document.getTargetChannel(),
                document -> safeNumber(document.getAverageDurationDays())
        );
    }

    private List<AggregationBucketResponse> groupAdTypesByCategory(List<AdTypeDocument> documents) {
        return buildBuckets(
                documents,
                document -> document.getCategory() == null ? "unknown" : document.getCategory(),
                document -> safeNumber(document.getAverageDurationDays())
        );
    }

    private List<AggregationBucketResponse> groupAdPhasesByResponsibleRole(List<AdPhaseDocument> documents) {
        return buildBuckets(
                documents,
                document -> document.getResponsibleRole() == null ? "unknown" : document.getResponsibleRole(),
                document -> safeNumber(document.getExpectedDurationHours())
        );
    }

    private double safeNumber(Number value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    private <T> List<AggregationBucketResponse> buildBuckets(
            List<T> documents,
            Function<T, String> keyExtractor,
            Function<T, Double> valueExtractor
    ) {
        return documents.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(keyExtractor))
                .entrySet()
                .stream()
                .map(entry -> {
                    DoubleSummaryStatistics stats = entry.getValue().stream()
                            .map(valueExtractor)
                            .filter(Objects::nonNull)
                            .collect(Collectors.summarizingDouble(Double::doubleValue));

                    return AggregationBucketResponse.builder()
                            .groupValue(entry.getKey())
                            .matchingDocumentsCount((long) entry.getValue().size())
                            .averageCalculatedValue(stats.getCount() == 0 ? null : stats.getAverage())
                            .build();
                })
                .sorted(Comparator.comparingLong(AggregationBucketResponse::getMatchingDocumentsCount).reversed()
                        .thenComparing(AggregationBucketResponse::getGroupValue))
                .toList();
    }
}
