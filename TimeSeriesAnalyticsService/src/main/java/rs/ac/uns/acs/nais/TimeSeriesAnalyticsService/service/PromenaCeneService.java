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
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.request.PromenaCeneRequest;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.response.PromenaCeneResponse;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.model.PromenaCenePoint;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromenaCeneService {

    private final InfluxDBClient influxDBClient;

    @Value("${influxdb.bucket}")
    private String bucket;

    @Value("${influxdb.org}")
    private String org;

    public PromenaCeneResponse create(PromenaCeneRequest request) {
        Instant timestamp = request.getTime() != null ? request.getTime() : Instant.now();
        double delta = request.getNovaCena() - request.getStaraCena();
        double procenat = (delta / request.getStaraCena()) * 100.0;

        PromenaCenePoint point = PromenaCenePoint.builder()
                .festivalId(request.getFestivalId())
                .nazivFestivala(request.getNazivFestivala())
                .tipKarte(request.getTipKarte())
                .razlog(request.getRazlog())
                .staraCena(request.getStaraCena())
                .novaCena(request.getNovaCena())
                .deltaCene(delta)
                .procenatPromene(procenat)
                .time(timestamp)
                .build();

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, point);

        return toResponse(point);
    }

    public void delete(DeleteRequest request) {
        DeleteApi deleteApi = influxDBClient.getDeleteApi();

        StringBuilder predicate = new StringBuilder("_measurement=\"promena_cene\"");
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

    public List<PromenaCeneResponse> findAll(String start, String stop) {
        String flux = String.format("""
                from(bucket: "%s")
                  |> range(start: %s, stop: %s)
                  |> filter(fn: (r) => r._measurement == "promena_cene")
                  |> pivot(rowKey: ["_time"], columnKey: ["_field"], valueColumn: "_value")
                """, bucket, start, stop);

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux, org);
        List<PromenaCeneResponse> results = new ArrayList<>();

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                results.add(recordToResponse(record));
            }
        }
        return results;
    }

    private PromenaCeneResponse toResponse(PromenaCenePoint point) {
        return PromenaCeneResponse.builder()
                .festivalId(point.getFestivalId())
                .nazivFestivala(point.getNazivFestivala())
                .tipKarte(point.getTipKarte())
                .razlog(point.getRazlog())
                .staraCena(point.getStaraCena())
                .novaCena(point.getNovaCena())
                .deltaCene(point.getDeltaCene())
                .procenatPromene(point.getProcenatPromene())
                .time(point.getTime())
                .build();
    }

    private PromenaCeneResponse recordToResponse(FluxRecord record) {
        return PromenaCeneResponse.builder()
                .festivalId((String) record.getValueByKey("festivalId"))
                .nazivFestivala((String) record.getValueByKey("nazivFestivala"))
                .tipKarte((String) record.getValueByKey("tipKarte"))
                .razlog((String) record.getValueByKey("razlog"))
                .staraCena(toDouble(record.getValueByKey("staraCena")))
                .novaCena(toDouble(record.getValueByKey("novaCena")))
                .deltaCene(toDouble(record.getValueByKey("deltaCene")))
                .procenatPromene(toDouble(record.getValueByKey("procenatPromene")))
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
