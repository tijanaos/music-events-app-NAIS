package rs.ac.uns.acs.nais.GraphDatabaseService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ReservationDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Reservation;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ReservationStatus;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.IReservationService;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation", description = "Reservation management endpoints")
public class ReservationController {

    private final IReservationService reservationService;

    @GetMapping
    @Operation(summary = "Get all reservations")
    public ResponseEntity<List<Reservation>> getAll() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by ID")
    public ResponseEntity<Reservation> getById(@PathVariable String id) {
        return ResponseEntity.ok(reservationService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new reservation")
    public ResponseEntity<Reservation> create(@RequestBody ReservationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update reservation note, details and status")
    public ResponseEntity<Reservation> update(@PathVariable String id, @RequestBody ReservationDTO dto) {
        return ResponseEntity.ok(reservationService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a reservation")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "Get reservations by status")
    public ResponseEntity<List<Reservation>> getByStatus(@PathVariable ReservationStatus status) {
        return ResponseEntity.ok(reservationService.findByStatus(status));
    }

    @GetMapping("/by-user/{createdBy}")
    @Operation(summary = "Get reservations by creator")
    public ResponseEntity<List<Reservation>> getByCreatedBy(@PathVariable String createdBy) {
        return ResponseEntity.ok(reservationService.findByCreatedBy(createdBy));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update reservation status only")
    public ResponseEntity<Reservation> updateStatus(
            @PathVariable String id,
            @RequestParam ReservationStatus status) {
        return ResponseEntity.ok(reservationService.updateStatus(id, status));
    }
}
