package rs.ac.uns.acs.nais.NegotiationAnalyticsService.configuration;

import com.influxdb.client.DeleteApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.exceptions.InfluxException;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.model.NegotiationStateHistory;
import rs.ac.uns.acs.nais.NegotiationAnalyticsService.model.OfferLifecycleEvent;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InfluxDBConnectionClass {

    @Value("${spring.influx.token}")
    private String token;

    @Value("${spring.influx.bucket}")
    private String bucket;

    @Value("${spring.influx.org}")
    private String org;

    @Value("${spring.influx.url}")
    private String url;

    public InfluxDBClient buildConnection() {
        return InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    }

    public String getToken() { return token; }
    public String getBucket() { return bucket; }
    public String getOrg() { return org; }
    public String getUrl() { return url; }

    // negotiation state -----------------------------

    public boolean saveNegotiationState(InfluxDBClient client, NegotiationStateHistory record) {
        try {
            WriteApiBlocking writeApi = client.getWriteApiBlocking();
            writeApi.writeMeasurement(WritePrecision.MS, record);
            return true;
        } catch (InfluxException e) {
            return false;
        }
    }

    // za dataseeder
    public boolean saveNegotiationStateBatch(InfluxDBClient client, List<NegotiationStateHistory> records) {
        try {
            WriteApiBlocking writeApi = client.getWriteApiBlocking();
            writeApi.writeMeasurements(WritePrecision.MS, records);
            return true;
        } catch (InfluxException e) {
            return false;
        }
    }

    public boolean deleteNegotiationStateByNegotiationId(InfluxDBClient client, String negotiationId) {
        try {
            DeleteApi deleteApi = client.getDeleteApi();
            OffsetDateTime start = OffsetDateTime.parse("1970-01-01T00:00:00Z");
            OffsetDateTime stop = OffsetDateTime.now().plus(1, ChronoUnit.DAYS);
            String predicate = "_measurement=\"negotiation_state_history\" AND negotiation_id=\"" + negotiationId + "\"";
            deleteApi.delete(start, stop, predicate, bucket, org);
            return true;
        } catch (InfluxException e) {
            return false;
        }
    }

    // offerlifecycle -----------------------------------

    public boolean saveOfferEvent(InfluxDBClient client, OfferLifecycleEvent record) {
        try {
            WriteApiBlocking writeApi = client.getWriteApiBlocking();
            writeApi.writeMeasurement(WritePrecision.MS, record);
            return true;
        } catch (InfluxException e) {
            return false;
        }
    }

    // za dataseeder
    public boolean saveOfferEventBatch(InfluxDBClient client, List<OfferLifecycleEvent> records) {
        try {
            WriteApiBlocking writeApi = client.getWriteApiBlocking();
            writeApi.writeMeasurements(WritePrecision.MS, records);
            return true;
        } catch (InfluxException e) {
            return false;
        }
    }

    public boolean deleteOfferEventsByOfferId(InfluxDBClient client, String offerId) {
        try {
            DeleteApi deleteApi = client.getDeleteApi();
            OffsetDateTime start = OffsetDateTime.parse("1970-01-01T00:00:00Z");
            OffsetDateTime stop = OffsetDateTime.now().plus(1, ChronoUnit.DAYS);
            String predicate = "_measurement=\"offer_lifecycle_events\" AND offer_id=\"" + offerId + "\"";
            deleteApi.delete(start, stop, predicate, bucket, org);
            return true;
        } catch (InfluxException e) {
            return false;
        }
    }

    // Upit 1: identifikacija uskih grla, u kom stanju pregovori prosecno najvise vremena provedu
    // vraca nazive stanja i prosecno vreme provedenu u njima u opadajucem redosledu
    public List<Map<String, Object>> avgDurationPerStateByTemplate(InfluxDBClient client, String templateName) {
        String flux = String.format("""
                from(bucket: "%s")
                |> range(start: 0, stop: 2026-05-31T00:00:00Z)
                |> filter(fn: (r) => r["_measurement"] == "negotiation_state_history")
                |> filter(fn: (r) => r["_field"] == "duration_seconds")
                |> filter(fn: (r) => r["template_name"] == "%s")
                |> group(columns: ["state_name"])
                |> mean()
                |> group()
                |> sort(columns: ["_value"], desc: true)
                |> yield(name: "avg_duration_per_state")
                """, bucket, templateName);

        List<Map<String, Object>> results = new ArrayList<>();
        QueryApi queryApi = client.getQueryApi();
        for (FluxTable table : queryApi.query(flux)) {
            for (FluxRecord record : table.getRecords()) {
                Map<String, Object> row = new HashMap<>();
                row.put("state_name", record.getValueByKey("state_name"));
                row.put("mean_duration_seconds", record.getValue());
                results.add(row);
            }
        }
        return results;
    }

    // Upit 2: mesecni trendovi, da li se broj uspesnih pregovora povecava kroz mesece
    // vraca broj propalih i broj uspesno zatvorenih pregovora po mesecima
    public List<Map<String, Object>> monthlyNegotiationSuccessTrend(InfluxDBClient client) {
        String flux = String.format("""
                from(bucket: "%s")
                  |> range(start: 0, stop: 2026-05-31T00:00:00Z)
                  |> filter(fn: (r) => r["_measurement"] == "negotiation_state_history")
                  |> filter(fn: (r) => r["_field"] == "duration_seconds")
                  |> filter(fn: (r) => r["negotiation_status"] == "CLOSED" or r["negotiation_status"] == "FAILED")
                  |> group(columns: ["negotiation_status"])
                  |> aggregateWindow(every: 1mo, fn: count, createEmpty: false)
                  |> sort(columns: ["_time"], desc: false)
                  |> yield(name: "monthly_trend")
                """, bucket);

        List<Map<String, Object>> results = new ArrayList<>();
        QueryApi queryApi = client.getQueryApi();
        for (FluxTable table : queryApi.query(flux)) {
            for (FluxRecord record : table.getRecords()) {
                Map<String, Object> row = new HashMap<>();
                row.put("month", record.getTime() != null ? record.getTime().toString() : null);
                row.put("negotiation_status", record.getValueByKey("negotiation_status"));
                row.put("count", record.getValue());
                results.add(row);
            }
        }
        return results;
    }

    // Upit 3: top lokacije po ukupnoj vrednosti zakljucenih ponuda
    // vraca lokacije sortirane opadajuce po ukupnoj sumi cena zatvorenih ponuda
    public List<Map<String, Object>> avgOfferPriceByEventTypeAndLocation(InfluxDBClient client) {
        String flux = String.format("""
                from(bucket: "%s")
                |> range(start: 0, stop: 2026-05-31T00:00:00Z)
                |> filter(fn: (r) => r["_measurement"] == "offer_lifecycle_events")
                |> filter(fn: (r) => r["_field"] == "price")
                |> filter(fn: (r) => r["event_type"] == "CLOSED")
                |> group(columns: ["location"])
                |> sum()
                |> group()
                |> sort(columns: ["_value"], desc: true)
                |> yield(name: "total_value_by_location")
                """, bucket);

        List<Map<String, Object>> results = new ArrayList<>();
        QueryApi queryApi = client.getQueryApi();
        for (FluxTable table : queryApi.query(flux)) {
            for (FluxRecord record : table.getRecords()) {
                Map<String, Object> row = new HashMap<>();
                row.put("location", record.getValueByKey("location"));
                row.put("total_price", record.getValue());
                results.add(row);
            }
        }
        return results;
    }

    public List<NegotiationStateHistory> findStatesByNegotiationId(InfluxDBClient client, String negotiationId) {
        String flux = String.format("""
                from(bucket: "%s")
                  |> range(start: 0)
                  |> filter(fn: (r) => r["_measurement"] == "negotiation_state_history")
                  |> filter(fn: (r) => r["negotiation_id"] == "%s")
                  |> pivot(rowKey: ["_time"], columnKey: ["_field"], valueColumn: "_value")
                """, bucket, negotiationId);

        return mapNegotiationStateRecords(client.getQueryApi(), flux);
    }

    public List<OfferLifecycleEvent> findEventsByOfferId(InfluxDBClient client, String offerId) {
        String flux = String.format("""
                from(bucket: "%s")
                  |> range(start: 0)
                  |> filter(fn: (r) => r["_measurement"] == "offer_lifecycle_events")
                  |> filter(fn: (r) => r["offer_id"] == "%s")
                  |> pivot(rowKey: ["_time"], columnKey: ["_field"], valueColumn: "_value")
                """, bucket, offerId);

        return mapOfferEventRecords(client.getQueryApi(), flux);
    }

    private List<NegotiationStateHistory> mapNegotiationStateRecords(QueryApi queryApi, String flux) {
        List<NegotiationStateHistory> results = new ArrayList<>();
        for (FluxTable table : queryApi.query(flux)) {
            for (FluxRecord r : table.getRecords()) {
                NegotiationStateHistory h = new NegotiationStateHistory();
                h.setNegotiation_id((String) r.getValueByKey("negotiation_id"));
                h.setState_name((String) r.getValueByKey("state_name"));
                h.setTemplate_name((String) r.getValueByKey("template_name"));
                h.setManager_id((String) r.getValueByKey("manager_id"));
                h.setNegotiation_status((String) r.getValueByKey("negotiation_status"));
                Object dur = r.getValueByKey("duration_seconds");
                if (dur instanceof Number) h.setDuration_seconds(((Number) dur).doubleValue());
                Object pn = r.getValueByKey("performer_name");
                if (pn instanceof String) h.setPerformer_name((String) pn);
                h.setEntered_at(r.getTime());
                results.add(h);
            }
        }
        return results;
    }

    private List<OfferLifecycleEvent> mapOfferEventRecords(QueryApi queryApi, String flux) {
        List<OfferLifecycleEvent> results = new ArrayList<>();
        for (FluxTable table : queryApi.query(flux)) {
            for (FluxRecord r : table.getRecords()) {
                OfferLifecycleEvent e = new OfferLifecycleEvent();
                e.setOffer_id((String) r.getValueByKey("offer_id"));
                e.setEvent_type((String) r.getValueByKey("event_type"));
                e.setManager_id((String) r.getValueByKey("manager_id"));
                e.setNegotiation_id((String) r.getValueByKey("negotiation_id"));
                Object price = r.getValueByKey("price");
                if (price instanceof Number) e.setPrice(((Number) price).doubleValue());
                Object loc = r.getValueByKey("location");
                if (loc instanceof String) e.setLocation((String) loc);
                e.setOccurred_at(r.getTime());
                results.add(e);
            }
        }
        return results;
    }
}
