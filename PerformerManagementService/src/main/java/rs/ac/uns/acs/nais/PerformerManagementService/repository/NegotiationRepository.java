package rs.ac.uns.acs.nais.PerformerManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.PerformerManagementService.model.Negotiation;

import java.util.List;

@Repository
public interface NegotiationRepository extends Neo4jRepository<Negotiation, String> {
    List<Negotiation> findByCreatedBy(String createdBy);
    List<Negotiation> findByFailReasonIsNotNull();
    List<Negotiation> findByConcludedAtIsNotNull();
}
