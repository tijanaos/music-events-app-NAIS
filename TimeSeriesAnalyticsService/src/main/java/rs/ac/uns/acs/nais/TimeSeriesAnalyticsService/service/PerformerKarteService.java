package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.model.PerformerKartePoint;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.saga.event.NegotiationConcludedEvent;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PerformerKarteService {

    private final InfluxDBClient influxDBClient;

    @Value("${influxdb.bucket}")
    private String bucket;

    @Value("${influxdb.org}")
    private String org;

    private static final double BROJ_KARATA = 5.0;

    public void writeAllocation(NegotiationConcludedEvent event) {
        PerformerKartePoint point = PerformerKartePoint.builder()
                .negotiationId(event.getNegotiationId())
                .performerId(event.getPerformerId())
                .performerName(event.getPerformerName())
                .brojKarata(BROJ_KARATA)
                .agreedFee(event.getAgreedFee())
                .time(Instant.now())
                .build();

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, point);
    }

}
