package rs.ac.uns.acs.nais.DynamicPricingService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.DynamicPricingService.model.Customer;

public interface CustomerRepository extends Neo4jRepository<Customer, String> {
}
