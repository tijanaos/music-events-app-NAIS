package rs.ac.uns.acs.nais.AdvertisingSearchService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.request.CreateAdSagaRequest;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.SagaStartResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.service.saga.AdCreationSagaService;

@RestController
@RequestMapping("/api/ad-sagas")
@RequiredArgsConstructor
public class AdCreationSagaController {

    private final AdCreationSagaService adCreationSagaService;

    @PostMapping("/ads")
    public ResponseEntity<SagaStartResponse> createAdWithType(@Valid @RequestBody CreateAdSagaRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(adCreationSagaService.startSaga(request));
    }
}
