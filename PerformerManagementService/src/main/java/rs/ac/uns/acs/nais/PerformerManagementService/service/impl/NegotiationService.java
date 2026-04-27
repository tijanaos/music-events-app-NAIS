package rs.ac.uns.acs.nais.PerformerManagementService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.acs.nais.PerformerManagementService.dto.NegotiationDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.model.*;
import rs.ac.uns.acs.nais.PerformerManagementService.model.enums.OfferStatus;
import rs.ac.uns.acs.nais.PerformerManagementService.model.relationship.CreatedFrom;
import rs.ac.uns.acs.nais.PerformerManagementService.model.relationship.InState;
import rs.ac.uns.acs.nais.PerformerManagementService.model.relationship.PartOf;
import rs.ac.uns.acs.nais.PerformerManagementService.repository.*;
import rs.ac.uns.acs.nais.PerformerManagementService.service.INegotiationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NegotiationService implements INegotiationService {

    private final NegotiationRepository negotiationRepository;
    private final OfferRepository offerRepository;
    private final PerformerRepository performerRepository;
    private final StateRepository stateRepository;
    private final Neo4jClient neo4jClient;

    @Override
    public List<Negotiation> findAll() {
        return negotiationRepository.findAll();
    }

    @Override
    public Negotiation findById(String id) {
        return negotiationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Negotiation not found with id: " + id));
    }

    @Override
    public Negotiation create(NegotiationDTO dto) {
        Offer offer = offerRepository.findById(dto.getOfferId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found with id: " + dto.getOfferId()));
        Performer performer = performerRepository.findById(dto.getPerformerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Performer not found with id: " + dto.getPerformerId()));
        State initialState = stateRepository.findById(dto.getInitialStateId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "State not found with id: " + dto.getInitialStateId()));

        if (!initialState.getIsInitial()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided state is not an initial state");
        }

        offer.setStatus(OfferStatus.ACCEPTED);
        offerRepository.save(offer);

        Negotiation negotiation = Negotiation.builder()
                .createdBy(dto.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .offer(CreatedFrom.builder().startedAt(LocalDateTime.now()).offer(offer).build())
                .currentState(InState.builder().enteredAt(LocalDateTime.now()).state(initialState).build())
                .performers(new ArrayList<>(List.of(
                        PartOf.builder().agreedFee(dto.getAgreedFee()).performer(performer).build()
                )))
                .build();

        return negotiationRepository.save(negotiation);
    }

    @Override
    public Negotiation advanceState(String id, String newStateId) {
        Negotiation existing = findById(id);
        if (existing.getConcludedAt() != null || existing.getFailReason() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot advance a closed negotiation");
        }
        State newState = stateRepository.findById(newStateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "State not found with id: " + newStateId));
        existing.setCurrentState(InState.builder().enteredAt(LocalDateTime.now()).state(newState).build());
        existing.setUpdatedAt(LocalDateTime.now());
        return negotiationRepository.save(existing);
    }

    @Override
    public Negotiation conclude(String id) {
        Negotiation existing = findById(id);
        State currentState = existing.getCurrentState().getState();
        if (!currentState.getIsFinal()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Negotiation can only be concluded from a final state");
        }
        existing.setConcludedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());
        return negotiationRepository.save(existing);
    }

    @Override
    public Negotiation fail(String id, String failReason) {
        Negotiation existing = findById(id);
        if (existing.getConcludedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot fail an already concluded negotiation");
        }
        existing.setFailReason(failReason);
        existing.setFailedAtStateName(existing.getCurrentState().getState().getName());
        existing.setUpdatedAt(LocalDateTime.now());
        return negotiationRepository.save(existing);
    }

    @Override
    public List<Negotiation> findByCreatedBy(String createdBy) {
        return negotiationRepository.findByCreatedBy(createdBy);
    }

    // Q1: Per manager — total negotiations and success rate
    @Override
    public List<Map<String, Object>> getManagerStats() {
        String query =
            "MATCH (n:Negotiation)-[:IN_STATE]->(s:State) " +
            "WITH n.createdBy AS manager, " +
            "     count(n) AS totalNegotiations, " +
            "     sum(CASE WHEN s.isFinal = true AND n.concludedAt IS NOT NULL THEN 1 ELSE 0 END) AS concluded " +
            "WITH manager, totalNegotiations, concluded, " +
            "     round(100.0 * concluded / totalNegotiations, 2) AS successRate " +
            "WHERE totalNegotiations > 0 " +
            "RETURN manager, totalNegotiations, concluded, successRate " +
            "ORDER BY successRate DESC";

        return new ArrayList<>(neo4jClient.query(query).fetch().all());
    }

    // Q3: Per offer — total negotiations, concluded, failed
    @Override
    public List<Map<String, Object>> getOfferStats() {
        String query =
            "MATCH (o:Offer) " +
            "OPTIONAL MATCH (n:Negotiation)-[:CREATED_FROM]->(o) " +
            "WITH o, " +
            "     count(DISTINCT n) AS totalNegotiations, " +
            "     sum(CASE WHEN n.concludedAt IS NOT NULL THEN 1 ELSE 0 END) AS concluded, " +
            "     sum(CASE WHEN n.failReason IS NOT NULL THEN 1 ELSE 0 END) AS failed " +
            "RETURN o.id AS offerId, o.price AS price, " +
            "       toString(o.status) AS offerStatus, " +
            "       totalNegotiations, concluded, failed " +
            "ORDER BY totalNegotiations DESC";

        return new ArrayList<>(neo4jClient.query(query).fetch().all());
    }

    // Q5: Stagnant negotiations — in current state longer than allowed
    @Override
    public List<Map<String, Object>> getStagnantNegotiations() {
        String query =
            "MATCH (n:Negotiation)-[r:IN_STATE]->(s:State) " +
            "WHERE s.isFinal = false " +
            "  AND n.concludedAt IS NULL " +
            "  AND n.failReason IS NULL " +
            "WITH n, s, r, " +
            "     duration.between(r.enteredAt, datetime()).days AS daysInState " +
            "WHERE daysInState > s.maxDurationDays " +
            "RETURN n.id AS negotiationId, n.createdBy AS manager, " +
            "       s.name AS currentState, daysInState, " +
            "       s.maxDurationDays AS allowedDays, " +
            "       (daysInState - s.maxDurationDays) AS daysOverdue " +
            "ORDER BY daysOverdue DESC";

        return new ArrayList<>(neo4jClient.query(query).fetch().all());
    }

    // Q2: Performers sorted by number of concluded negotiations
    @Override
    public List<Map<String, Object>> getPerformerSuccessStats() {
        String query =
            "MATCH (p:Performer)-[:PART_OF]->(n:Negotiation)-[:IN_STATE]->(s:State) " +
            "WHERE s.isFinal = true AND n.concludedAt IS NOT NULL " +
            "WITH p, count(n) AS concludedCount " +
            "RETURN p.id AS performerId, p.name AS performerName, " +
            "       p.genre AS genre, concludedCount " +
            "ORDER BY concludedCount DESC";

        return new ArrayList<>(neo4jClient.query(query).fetch().all());
    }

    // Q4: Average agreed fee and negotiation count per genre
    @Override
    public List<Map<String, Object>> getGenreStats() {
        String query =
            "MATCH (p:Performer)-[r:PART_OF]->(n:Negotiation) " +
            "WITH p.genre AS genre, " +
            "     count(n) AS negotiationCount, " +
            "     avg(r.agreedFee) AS avgAgreedFee " +
            "WHERE negotiationCount >= 2 " +
            "RETURN genre, negotiationCount, " +
            "       round(avgAgreedFee, 2) AS avgAgreedFee " +
            "ORDER BY negotiationCount DESC";

        return new ArrayList<>(neo4jClient.query(query).fetch().all());
    }
}
