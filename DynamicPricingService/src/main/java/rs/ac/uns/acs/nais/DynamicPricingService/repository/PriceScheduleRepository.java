package rs.ac.uns.acs.nais.DynamicPricingService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.DynamicPricingService.model.PriceSchedule;

public interface PriceScheduleRepository extends Neo4jRepository<PriceSchedule, String> {
}
