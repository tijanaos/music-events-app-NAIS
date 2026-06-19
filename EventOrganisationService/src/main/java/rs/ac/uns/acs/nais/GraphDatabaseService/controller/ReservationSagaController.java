package rs.ac.uns.acs.nais.GraphDatabaseService.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.RequiresResourceDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ReservationDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.SagaInstance;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.SagaOrchestrator;

import java.util.List;
import java.util.Map;

/**
 * REST kontroler koji izlaze endpoint-e za pokretanje transakcione obrade
 * (Saga, orkestracija) kreiranja rezervacije.
 *
 * Tok:
 *   1. SagaOrchestrator salje CreateReservationCommand Neo4j CommandListener-u.
 *   2. Neo4j kreira Reservation i povezane resurse, salje ReservationCreatedReply.
 *   3. SagaOrchestrator salje RecordResourceUsageCommand Analytics CommandListener-u.
 *   4. Analytics servis upisuje ResourceUsageDocument-e u Elasticsearch i salje reply.
 *   5a. Uspeh: stanje sage = COMPLETED.
 *   5b. Neuspeh: SagaOrchestrator salje DeleteReservationCommand (kompenzacija).
 */
@Slf4j
@RestController
@RequestMapping("/api/saga/reservation")
@RequiredArgsConstructor
public class ReservationSagaController {

    private final SagaOrchestrator sagaOrchestrator;

    /**
     * Pokrece orkestrisanu sagu kreiranja rezervacije.
     *
     * Telo zahteva:
     * {
     *   "reservation": { ...ReservationDTO polja... },
     *   "requestedResources": [ { ...RequiresResourceDTO polja... } ]
     * }
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> startReservationSaga(@RequestBody StartSagaRequest request) {
        log.info("[CONTROLLER] POST /api/saga/reservation -- pokretanje sage");

        try {
            String sagaId = sagaOrchestrator.startSaga(request.getReservation(), request.getRequestedResources());

            return ResponseEntity.ok(Map.of(
                    "sagaId", sagaId,
                    "status", "STARTED",
                    "message", "Saga kreiranja rezervacije je pokrenuta. Status proveriti na GET /api/saga/reservation/status/" + sagaId));

        } catch (Exception e) {
            log.error("[CONTROLLER] GRESKA pri pokretanju sage: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /** Vraca trenutno stanje instance sage, za polling i debagovanje. */
    @GetMapping("/status/{sagaId}")
    public ResponseEntity<?> getSagaStatus(@PathVariable String sagaId) {
        log.info("[CONTROLLER] GET /api/saga/reservation/status/{} -- citanje stanja sage", sagaId);

        SagaInstance instance = sagaOrchestrator.getSagaStatus(sagaId);
        if (instance == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Saga sa ID=" + sagaId + " nije pronadjena"));
        }

        return ResponseEntity.ok(Map.of(
                "sagaId", instance.getSagaId(),
                "state", instance.getState().name(),
                "reservationId", instance.getReservationId() == null ? "" : instance.getReservationId(),
                "createdAt", instance.getCreatedAt().toString()));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StartSagaRequest {
        private ReservationDTO reservation;
        private List<RequiresResourceDTO> requestedResources;
    }
}