package rs.ac.uns.acs.nais.PerformerManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.PerformerManagementService.model.Offer;
import rs.ac.uns.acs.nais.PerformerManagementService.model.enums.OfferStatus;

import java.util.List;

@Repository
public interface OfferRepository extends Neo4jRepository<Offer, String> {

    List<Offer> findByStatus(OfferStatus status);
}
