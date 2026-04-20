package rs.ac.uns.acs.nais.GraphDatabaseService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.TimeSlotDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.TimeSlot;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.SlotType;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.TimeSlotStatus;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.ITimeSlotService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/timeslots")
@RequiredArgsConstructor
@Tag(name = "TimeSlot", description = "TimeSlot management endpoints")
public class TimeSlotController {

    private final ITimeSlotService timeSlotService;

    @GetMapping
    @Operation(summary = "Get all time slots")
    public ResponseEntity<List<TimeSlot>> getAll() {
        return ResponseEntity.ok(timeSlotService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get time slot by ID")
    public ResponseEntity<TimeSlot> getById(@PathVariable String id) {
        return ResponseEntity.ok(timeSlotService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new time slot")
    public ResponseEntity<TimeSlot> create(@RequestBody TimeSlotDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(timeSlotService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing time slot")
    public ResponseEntity<TimeSlot> update(@PathVariable String id, @RequestBody TimeSlotDTO dto) {
        return ResponseEntity.ok(timeSlotService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a time slot")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        timeSlotService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "Get time slots by status")
    public ResponseEntity<List<TimeSlot>> getByStatus(@PathVariable TimeSlotStatus status) {
        return ResponseEntity.ok(timeSlotService.findByStatus(status));
    }

    @GetMapping("/by-date/{date}")
    @Operation(summary = "Get time slots by date")
    public ResponseEntity<List<TimeSlot>> getByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(timeSlotService.findByDate(date));
    }

    @GetMapping("/by-type/{slotType}")
    @Operation(summary = "Get time slots by slot type")
    public ResponseEntity<List<TimeSlot>> getBySlotType(@PathVariable SlotType slotType) {
        return ResponseEntity.ok(timeSlotService.findBySlotType(slotType));
    }

    @GetMapping("/free")
    @Operation(summary = "Get all free time slots")
    public ResponseEntity<List<TimeSlot>> getFree() {
        return ResponseEntity.ok(timeSlotService.findFree());
    }
}
