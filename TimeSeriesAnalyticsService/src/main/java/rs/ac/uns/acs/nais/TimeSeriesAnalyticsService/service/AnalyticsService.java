package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.response.analytics.NedeljniPrihodResponse;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.response.analytics.PeakSatiResponse;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.response.analytics.RastCenaResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final InfluxDBClient influxDBClient;

    @Value("${influxdb.bucket}")
    private String bucket;

    @Value("${influxdb.org}")
    private String org;

    /*
     * Upit 1: Nedeljni prihod po tipu karte i tieru kupca
     *
     * Kombinuje:
     * - FILTRIRANJE: po opsegu datuma i isključenju promo kupovina
     * - GRUPISANJE: po tipKarte, tierKupca i vremenskom prozoru od 1 nedelje
     * - AGREGACIJA: suma ukupne cene (prihod) i suma količine (broj karata)
     * - SORTIRANJE: po ukupnom prihodu opadajuće
     */
    public List<NedeljniPrihodResponse> getNedeljniPrihodPoTipuITieru(String start, String stop, boolean ukljuciPromo) {
        String promoFilter = ukljuciPromo
                ? ""
                : "|> filter(fn: (r) => r.promoKoriscen == \"false\")";

        String prihodFlux = String.format("""
                from(bucket: "%s")
                  |> range(start: %s, stop: %s)
                  |> filter(fn: (r) => r._measurement == "kupovina" and r._field == "ukupnaCena")
                  %s
                  |> aggregateWindow(every: 1w, fn: sum, createEmpty: false)
                  |> group(columns: ["tipKarte", "tierKupca", "_time"])
                  |> sum()
                  |> sort(columns: ["_value"], desc: true)
                """, bucket, start, stop, promoFilter);

        String kolicineFlux = String.format("""
                from(bucket: "%s")
                  |> range(start: %s, stop: %s)
                  |> filter(fn: (r) => r._measurement == "kupovina" and r._field == "kolicina")
                  %s
                  |> aggregateWindow(every: 1w, fn: sum, createEmpty: false)
                  |> group(columns: ["tipKarte", "tierKupca", "_time"])
                  |> sum()
                """, bucket, start, stop, promoFilter);

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<NedeljniPrihodResponse> results = new ArrayList<>();

        List<FluxTable> prihodTables = queryApi.query(prihodFlux, org);
        List<FluxTable> kolicineTables = queryApi.query(kolicineFlux, org);

        for (int t = 0; t < prihodTables.size(); t++) {
            List<FluxRecord> prihodRecords = prihodTables.get(t).getRecords();
            List<FluxRecord> kolicineRecords = t < kolicineTables.size()
                    ? kolicineTables.get(t).getRecords()
                    : List.of();

            for (int r = 0; r < prihodRecords.size(); r++) {
                FluxRecord pr = prihodRecords.get(r);
                Double kolicina = r < kolicineRecords.size()
                        ? toDouble(kolicineRecords.get(r).getValue())
                        : null;

                results.add(NedeljniPrihodResponse.builder()
                        .tipKarte((String) pr.getValueByKey("tipKarte"))
                        .tierKupca((String) pr.getValueByKey("tierKupca"))
                        .nedelja(pr.getTime())
                        .ukupanPrihod(toDouble(pr.getValue()))
                        .ukupnoKarata(kolicina)
                        .build());
            }
        }
        return results;
    }

    /*
     * Upit 2: Rang kupaca po ukupnoj potrošnji sa brojem transakcija po tipu karte
     *
     * Kombinuje:
     * - FILTRIRANJE: po vremenskom opsegu i tipu karte (opciono)
     * - GRUPISANJE: po tierKupca i tipKarte
     * - AGREGACIJA: suma prihoda, broj transakcija (count), prosečna vrednost porudžbine
     * - SORTIRANJE: po ukupnom prihodu opadajuće — pokazuje koji tier + tip karte donosi najviše prihoda
     */
    public List<PeakSatiResponse> getRangKupacaPoTrosnji(String start, String stop, String tipKarte) {
        String tipKarteFilter = (tipKarte != null && !tipKarte.isEmpty())
                ? String.format("|> filter(fn: (r) => r.tipKarte == \"%s\")", tipKarte)
                : "";

        String prihodFlux = String.format("""
                from(bucket: "%s")
                  |> range(start: %s, stop: %s)
                  |> filter(fn: (r) => r._measurement == "kupovina" and r._field == "ukupnaCena")
                  %s
                  |> group(columns: ["tierKupca", "tipKarte"])
                  |> sum()
                  |> sort(columns: ["_value"], desc: true)
                """, bucket, start, stop, tipKarteFilter);

        String countFlux = String.format("""
                from(bucket: "%s")
                  |> range(start: %s, stop: %s)
                  |> filter(fn: (r) => r._measurement == "kupovina" and r._field == "ukupnaCena")
                  %s
                  |> group(columns: ["tierKupca", "tipKarte"])
                  |> count()
                  |> sort(columns: ["_value"], desc: true)
                """, bucket, start, stop, tipKarteFilter);

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<PeakSatiResponse> results = new ArrayList<>();

        List<FluxTable> prihodTables = queryApi.query(prihodFlux, org);
        List<FluxTable> countTables = queryApi.query(countFlux, org);

        for (int t = 0; t < prihodTables.size(); t++) {
            List<FluxRecord> prihodRecords = prihodTables.get(t).getRecords();
            List<FluxRecord> countRecords = t < countTables.size()
                    ? countTables.get(t).getRecords()
                    : List.of();

            for (int r = 0; r < prihodRecords.size(); r++) {
                FluxRecord pr = prihodRecords.get(r);
                Double ukupanPrihod = toDouble(pr.getValue());
                Long broj = r < countRecords.size()
                        ? toLong(countRecords.get(r).getValue())
                        : null;
                Double prosecna = (ukupanPrihod != null && broj != null && broj > 0)
                        ? ukupanPrihod / broj
                        : null;

                results.add(PeakSatiResponse.builder()
                        .tierKupca((String) pr.getValueByKey("tierKupca"))
                        .tipKarte((String) pr.getValueByKey("tipKarte"))
                        .ukupanPrihod(ukupanPrihod)
                        .brojTransakcija(broj)
                        .prosecnaVrednostPorudzbine(prosecna)
                        .build());
            }
        }
        return results;
    }

    /*
     * Upit 3: Mesečni rast cena po tipu karte za promene uzrokovane povećanjem tražnje
     *
     * Kombinuje:
     * - FILTRIRANJE: po vremenskom opsegu i razlogu promene cene (POVECANJE_TRAZNJE)
     * - GRUPISANJE: po tipKarte, festivalId i mesečnom vremenskom prozoru
     * - AGREGACIJA: suma delta cene, prosečan procenat promene, broj promena
     * - SORTIRANJE: u Fluxu hronološki po mesecu unutar svake grupe (tipKarte+festival),
     *               u Javi finalno sortiranje po tipKarte pa po mesecu — rezultat prikazuje
     *               kompletnu vremensku istoriju promene cene za svaki tip karte redom
     */
    public List<RastCenaResponse> getMesecniRastCenaPoTipu(String start, String stop, String razlog) {
        String razlogFilter = (razlog != null && !razlog.isEmpty())
                ? String.format("|> filter(fn: (r) => r.razlog == \"%s\")", razlog)
                : "|> filter(fn: (r) => r.razlog == \"POVECANJE_TRAZNJE\")";

        String deltaFlux = String.format("""
                from(bucket: "%s")
                  |> range(start: %s, stop: %s)
                  |> filter(fn: (r) => r._measurement == "promena_cene" and r._field == "deltaCene")
                  %s
                  |> aggregateWindow(every: 1mo, fn: sum, createEmpty: false)
                  |> group(columns: ["tipKarte", "festivalId", "nazivFestivala"])
                  |> sort(columns: ["_time"], desc: false)
                """, bucket, start, stop, razlogFilter);

        String procenatFlux = String.format("""
                from(bucket: "%s")
                  |> range(start: %s, stop: %s)
                  |> filter(fn: (r) => r._measurement == "promena_cene" and r._field == "procenatPromene")
                  %s
                  |> aggregateWindow(every: 1mo, fn: mean, createEmpty: false)
                  |> group(columns: ["tipKarte", "festivalId"])
                  |> sort(columns: ["_time"], desc: false)
                """, bucket, start, stop, razlogFilter);

        String countFlux = String.format("""
                from(bucket: "%s")
                  |> range(start: %s, stop: %s)
                  |> filter(fn: (r) => r._measurement == "promena_cene" and r._field == "deltaCene")
                  %s
                  |> aggregateWindow(every: 1mo, fn: count, createEmpty: false)
                  |> group(columns: ["tipKarte", "festivalId"])
                  |> sort(columns: ["_time"], desc: false)
                """, bucket, start, stop, razlogFilter);

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<RastCenaResponse> results = new ArrayList<>();

        List<FluxTable> deltaTables = queryApi.query(deltaFlux, org);
        List<FluxTable> procenatTables = queryApi.query(procenatFlux, org);
        List<FluxTable> countTables = queryApi.query(countFlux, org);

        for (int t = 0; t < deltaTables.size(); t++) {
            List<FluxRecord> deltaRecords = deltaTables.get(t).getRecords();
            List<FluxRecord> procenatRecords = t < procenatTables.size()
                    ? procenatTables.get(t).getRecords()
                    : List.of();
            List<FluxRecord> countRecords = t < countTables.size()
                    ? countTables.get(t).getRecords()
                    : List.of();

            for (int r = 0; r < deltaRecords.size(); r++) {
                FluxRecord dr = deltaRecords.get(r);
                Double procenat = r < procenatRecords.size()
                        ? toDouble(procenatRecords.get(r).getValue())
                        : null;
                Long broj = r < countRecords.size()
                        ? toLong(countRecords.get(r).getValue())
                        : null;

                results.add(RastCenaResponse.builder()
                        .tipKarte((String) dr.getValueByKey("tipKarte"))
                        .festivalId((String) dr.getValueByKey("festivalId"))
                        .nazivFestivala((String) dr.getValueByKey("nazivFestivala"))
                        .mesec(dr.getTime())
                        .ukupnaDeltaCene(toDouble(dr.getValue()))
                        .prosecniProcenatPromene(procenat)
                        .brojPromena(broj)
                        .build());
            }
        }

        results.sort(Comparator
                .comparing(RastCenaResponse::getTipKarte)
                .thenComparing(RastCenaResponse::getFestivalId)
                .thenComparing(Comparator.nullsLast(
                        Comparator.comparing(RastCenaResponse::getMesec))));

        return results;
    }

    private Double toDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Double d) return d;
        if (value instanceof Number n) return n.doubleValue();
        return null;
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long l) return l;
        if (value instanceof Number n) return n.longValue();
        return null;
    }
}
