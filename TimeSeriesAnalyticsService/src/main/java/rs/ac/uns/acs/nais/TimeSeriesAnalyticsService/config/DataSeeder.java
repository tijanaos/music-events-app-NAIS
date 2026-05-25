package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.model.KupovinaPoint;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.model.PromenaCenePoint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final InfluxDBClient influxDBClient;

    @Value("${influxdb.bucket}")
    private String bucket;

    @Value("${influxdb.org}")
    private String influxOrg;

    @Override
    public void run(String... args) throws Exception {
        if (isAlreadySeeded()) {
            log.info("InfluxDB already seeded, skipping.");
            return;
        }

        log.info("Seeding InfluxDB from CSV files...");
        int k = seedKupovine();
        int p = seedPromeneCena();
        log.info("Seeding complete: {} kupovina, {} promena_cene records written.", k, p);
    }

    private boolean isAlreadySeeded() {
        String check = String.format("""
                from(bucket: "%s")
                  |> range(start: -3y)
                  |> filter(fn: (r) => r._measurement == "kupovina")
                  |> limit(n: 1)
                """, bucket);
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(check, influxOrg);
        return tables.stream().anyMatch(t -> !t.getRecords().isEmpty());
    }

    private int seedKupovine() throws Exception {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        List<KupovinaPoint> batch = new ArrayList<>();
        int total = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("data/kupovina_data.csv").getInputStream()))) {

            reader.readLine(); // skip header

            String line;
            while ((line = reader.readLine()) != null) {
                String[] c = line.split(",");
                if (c.length < 9) continue;

                KupovinaPoint point = KupovinaPoint.builder()
                        .time(Instant.parse(c[0].trim()))
                        .festivalId(c[1].trim())
                        .nazivFestivala(c[2].trim())
                        .tipKarte(c[3].trim())
                        .tierKupca(c[4].trim())
                        .promoKoriscen(c[5].trim())
                        .kolicina(Double.parseDouble(c[6].trim()))
                        .ukupnaCena(Double.parseDouble(c[7].trim()))
                        .cenaPoPKarti(Double.parseDouble(c[8].trim()))
                        .build();

                batch.add(point);
                total++;

                if (batch.size() >= 200) {
                    writeApi.writeMeasurements(bucket, influxOrg, WritePrecision.NS, batch);
                    batch.clear();
                }
            }
        }

        if (!batch.isEmpty()) {
            writeApi.writeMeasurements(bucket, influxOrg, WritePrecision.NS, batch);
        }
        return total;
    }

    private int seedPromeneCena() throws Exception {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        List<PromenaCenePoint> batch = new ArrayList<>();
        int total = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("data/promena_cene_data.csv").getInputStream()))) {

            reader.readLine(); // skip header

            String line;
            while ((line = reader.readLine()) != null) {
                String[] c = line.split(",");
                if (c.length < 9) continue;

                PromenaCenePoint point = PromenaCenePoint.builder()
                        .time(Instant.parse(c[0].trim()))
                        .festivalId(c[1].trim())
                        .nazivFestivala(c[2].trim())
                        .tipKarte(c[3].trim())
                        .razlog(c[4].trim())
                        .staraCena(Double.parseDouble(c[5].trim()))
                        .novaCena(Double.parseDouble(c[6].trim()))
                        .deltaCene(Double.parseDouble(c[7].trim()))
                        .procenatPromene(Double.parseDouble(c[8].trim()))
                        .build();

                batch.add(point);
                total++;

                if (batch.size() >= 200) {
                    writeApi.writeMeasurements(bucket, influxOrg, WritePrecision.NS, batch);
                    batch.clear();
                }
            }
        }

        if (!batch.isEmpty()) {
            writeApi.writeMeasurements(bucket, influxOrg, WritePrecision.NS, batch);
        }
        return total;
    }
}
