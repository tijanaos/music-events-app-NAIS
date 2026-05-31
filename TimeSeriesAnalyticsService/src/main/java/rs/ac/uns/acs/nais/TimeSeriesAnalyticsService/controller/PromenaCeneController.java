package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.request.DeleteRequest;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.request.PromenaCeneRequest;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.response.PromenaCeneResponse;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.service.PromenaCeneService;

import java.util.List;

@RestController
@RequestMapping("/api/promene-cene")
@RequiredArgsConstructor
public class PromenaCeneController {

    private final PromenaCeneService promenaCeneService;

    @PostMapping
    public ResponseEntity<PromenaCeneResponse> create(@RequestBody PromenaCeneRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(promenaCeneService.create(request));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody DeleteRequest request) {
        promenaCeneService.delete(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PromenaCeneResponse>> findAll(
            @RequestParam(defaultValue = "-1y") String start,
            @RequestParam(defaultValue = "now()") String stop) {
        return ResponseEntity.ok(promenaCeneService.findAll(start, stop));
    }
}
