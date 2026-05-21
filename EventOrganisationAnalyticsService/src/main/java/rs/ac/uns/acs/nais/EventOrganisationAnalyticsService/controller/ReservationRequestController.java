package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request.ReservationRequestDto;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ReservationRequestResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service.ReservationRequestService;

import java.util.List;

@RestController
@RequestMapping("/reservation-requests")
@RequiredArgsConstructor
public class ReservationRequestController {

    private final ReservationRequestService service;

    @PostMapping
    public ResponseEntity<ReservationRequestResponse> create(@Valid @RequestBody ReservationRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationRequestResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<ReservationRequestResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/by-bina/{binaId}")
    public ResponseEntity<List<ReservationRequestResponse>> findByBina(@PathVariable String binaId) {
        return ResponseEntity.ok(service.findByBinaId(binaId));
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<ReservationRequestResponse>> findByStatus(@RequestParam String status) {
        return ResponseEntity.ok(service.findByStatus(status));
    }

    @GetMapping("/by-izvodjac/{izvodjacId}")
    public ResponseEntity<List<ReservationRequestResponse>> findByIzvodjac(@PathVariable String izvodjacId) {
        return ResponseEntity.ok(service.findByIzvodjacId(izvodjacId));
    }

    @GetMapping("/with-tasks")
    public ResponseEntity<List<ReservationRequestResponse>> findWithTasks() {
        return ResponseEntity.ok(service.findWithTasks());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationRequestResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ReservationRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
