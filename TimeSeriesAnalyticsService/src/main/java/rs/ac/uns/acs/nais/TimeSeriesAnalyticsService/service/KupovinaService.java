package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.service;

import com.influxdb.client.DeleteApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.request.DeleteRequest;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.request.KupovinaRequest;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.response.KupovinaResponse;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.model.KupovinaPoint;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KupovinaService {

    private final InfluxDBClient influxDBClient;

    @Value("${influxdb.bucket}")
    private String bucket;

    @Value("${influxdb.org}")
    private String org;

    public KupovinaResponse create(KupovinaRequest request) {
        Instant timestamp = request.getTime() != null ? request.getTime() : Instant.now();
        double cenaPo = request.getUkupnaCena() / request.getKolicina();

        KupovinaPoint point = KupovinaPoint.builder()
                .festivalId(request.getFestivalId())
                .nazivFestivala(request.getNazivFestivala())
                .tipKarte(request.getTipKarte())
                .tierKupca(request.getTierKupca())
                .promoKoriscen(request.isPromoKoriscen() ? "true" : "false")
                .kolicina(request.getKolicina())
                .ukupnaCena(request.getUkupnaCena())
                .cenaPoPKarti(cenaPo)
                .time(timestamp)
                .build();

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, point);

        return toResponse(point);
    }

    public void delete(DeleteRequest request) {
        DeleteApi deleteApi = influxDBClient.getDeleteApi();

        StringBuilder predicate = new StringBuilder("_measurement=\"kupovina\"");
        if (request.getFestivalId() != null) {
            predicate.append(" AND festivalId=\"").append(request.getFestivalId()).append("\"");
        }
        if (request.getTipKarte() != null) {
            predicate.append(" AND tipKarte=\"").append(request.getTipKarte()).append("\"");
        }

        OffsetDateTime start = OffsetDateTime.ofInstant(request.getStart(), ZoneOffset.UTC);
        OffsetDateTime stop = OffsetDateTime.ofInstant(request.getStop(), ZoneOffset.UTC);
        deleteApi.delete(start, stop, predicate.toString(), bucket, org);
    }

    public List<KupovinaResponse> findAll(String start, String stop) {
        String flux = String.format("""
                from(bucket: "%s")
                  |> range(start: %s, stop: %s)
                  |> filter(fn: (r) => r._measurement == "kupovina")
                  |> pivot(rowKey: ["_time"], columnKey: ["_field"], valueColumn: "_value")
                """, bucket, start, stop);

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux, org);
        List<KupovinaResponse> results = new ArrayList<>();

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                results.add(recordToResponse(record));
            }
        }
        return results;
    }

    private KupovinaResponse toResponse(KupovinaPoint point) {
        return KupovinaResponse.builder()
                .festivalId(point.getFestivalId())
                .nazivFestivala(point.getNazivFestivala())
                .tipKarte(point.getTipKarte())
                .tierKupca(point.getTierKupca())
                .promoKoriscen(point.getPromoKoriscen())
                .kolicina(point.getKolicina())
                .ukupnaCena(point.getUkupnaCena())
                .cenaPoPKarti(point.getCenaPoPKarti())
                .time(point.getTime())
                .build();
    }

    private KupovinaResponse recordToResponse(FluxRecord record) {
        return KupovinaResponse.builder()
                .festivalId((String) record.getValueByKey("festivalId"))
                .nazivFestivala((String) record.getValueByKey("nazivFestivala"))
                .tipKarte((String) record.getValueByKey("tipKarte"))
                .tierKupca((String) record.getValueByKey("tierKupca"))
                .promoKoriscen((String) record.getValueByKey("promoKoriscen"))
                .kolicina(toDouble(record.getValueByKey("kolicina")))
                .ukupnaCena(toDouble(record.getValueByKey("ukupnaCena")))
                .cenaPoPKarti(toDouble(record.getValueByKey("cenaPoPKarti")))
                .time(record.getTime())
                .build();
    }

    private Double toDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Double d) return d;
        if (value instanceof Number n) return n.doubleValue();
        return null;
    }
}
