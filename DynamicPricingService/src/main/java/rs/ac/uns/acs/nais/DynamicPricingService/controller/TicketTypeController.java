package rs.ac.uns.acs.nais.DynamicPricingService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.request.TicketTypeRequest;
import rs.ac.uns.acs.nais.DynamicPricingService.dto.response.TicketTypeResponse;
import rs.ac.uns.acs.nais.DynamicPricingService.service.TicketTypeService;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-types")
@RequiredArgsConstructor
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;

    @GetMapping
    public ResponseEntity<List<TicketTypeResponse>> getAll() {
        return ResponseEntity.ok(ticketTypeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketTypeResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(ticketTypeService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TicketTypeResponse> create(@RequestBody TicketTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketTypeService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketTypeResponse> update(@PathVariable String id, @RequestBody TicketTypeRequest request) {
        return ResponseEntity.ok(ticketTypeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        ticketTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
