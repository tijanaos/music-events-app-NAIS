package rs.ac.uns.acs.nais.PerformerManagementService.service;

import rs.ac.uns.acs.nais.PerformerManagementService.dto.NegotiationDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.model.Negotiation;

import java.util.List;
import java.util.Map;

public interface INegotiationService {
    List<Negotiation> findAll();
    Negotiation findById(String id);
    Negotiation create(NegotiationDTO dto);
    Negotiation conclude(String id);
    Negotiation fail(String id, String failReason);
    Negotiation advanceState(String id, String newStateId);
    List<Negotiation> findByCreatedBy(String createdBy);
    List<Map<String, Object>> getManagerStats();
    List<Map<String, Object>> getOfferStats();
    List<Map<String, Object>> getStagnantNegotiations();
    List<Map<String, Object>> getPerformerSuccessStats();
    List<Map<String, Object>> getGenreStats();
}
