package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.response.analytics.NedeljniPrihodResponse;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.response.analytics.PeakSatiResponse;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.response.analytics.RastCenaResponse;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.service.AnalyticsService;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/nedeljni-prihod")
    public ResponseEntity<List<NedeljniPrihodResponse>> getNedeljniPrihod(
            @RequestParam(defaultValue = "-6mo") String start,
            @RequestParam(defaultValue = "now()") String stop,
            @RequestParam(defaultValue = "true") boolean ukljuciPromo) {
        return ResponseEntity.ok(analyticsService.getNedeljniPrihodPoTipuITieru(start, stop, ukljuciPromo));
    }

    @GetMapping("/rang-kupaca")
    public ResponseEntity<List<PeakSatiResponse>> getRangKupaca(
            @RequestParam(defaultValue = "-6mo") String start,
            @RequestParam(defaultValue = "now()") String stop,
            @RequestParam(required = false) String tipKarte) {
        return ResponseEntity.ok(analyticsService.getRangKupacaPoTrosnji(start, stop, tipKarte));
    }

    @GetMapping("/rast-cena")
    public ResponseEntity<List<RastCenaResponse>> getRastCena(
            @RequestParam(defaultValue = "-1y") String start,
            @RequestParam(defaultValue = "now()") String stop,
            @RequestParam(required = false) String razlog) {
        return ResponseEntity.ok(analyticsService.getMesecniRastCenaPoTipu(start, stop, razlog));
    }
}
