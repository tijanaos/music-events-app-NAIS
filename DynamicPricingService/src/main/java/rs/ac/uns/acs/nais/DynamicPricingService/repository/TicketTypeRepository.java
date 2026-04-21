package rs.ac.uns.acs.nais.DynamicPricingService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.DynamicPricingService.model.TicketType;

public interface TicketTypeRepository extends Neo4jRepository<TicketType, String> {
}
