package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.request.DeleteRequest;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.request.KupovinaRequest;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.response.KupovinaResponse;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.service.KupovinaService;

import java.util.List;

@RestController
@RequestMapping("/api/kupovine")
@RequiredArgsConstructor
public class KupovinaController {

    private final KupovinaService kupovinaService;

    @PostMapping
    public ResponseEntity<KupovinaResponse> create(@RequestBody KupovinaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(kupovinaService.create(request));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody DeleteRequest request) {
        kupovinaService.delete(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<KupovinaResponse>> findAll(
            @RequestParam(defaultValue = "-1y") String start,
            @RequestParam(defaultValue = "now()") String stop) {
        return ResponseEntity.ok(kupovinaService.findAll(start, stop));
    }
}
