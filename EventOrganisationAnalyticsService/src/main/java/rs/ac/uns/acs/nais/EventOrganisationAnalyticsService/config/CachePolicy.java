package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.config;

import java.time.LocalDate;

public final class CachePolicy {

    private static final String LAST_MONTH = "last-month";
    private static final String LAST_YEAR = "last-year";
    private static final String ALL_STAGES = "all";

    private CachePolicy() {
    }

    public static boolean isStandardReportingPeriod(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();
        return to != null
                && from != null
                && to.isEqual(today)
                && (from.isEqual(today.minusMonths(1)) || from.isEqual(today.minusYears(1)));
    }

    public static String standardReportingPeriodKey(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();
        if (to == null || from == null || !to.isEqual(today)) {
            return from + ":" + to;
        }
        if (from.isEqual(today.minusMonths(1))) {
            return LAST_MONTH + ":" + from + ":" + to;
        }
        if (from.isEqual(today.minusYears(1))) {
            return LAST_YEAR + ":" + from + ":" + to;
        }
        return from + ":" + to;
    }

    public static String normalizeStageId(String stageId) {
        return stageId == null || stageId.isBlank() ? ALL_STAGES : stageId;
    }
}
