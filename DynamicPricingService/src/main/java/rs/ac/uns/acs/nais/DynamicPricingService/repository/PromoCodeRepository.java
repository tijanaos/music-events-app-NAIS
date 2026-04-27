package rs.ac.uns.acs.nais.DynamicPricingService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.DynamicPricingService.model.PromoCode;

import java.util.Optional;

public interface PromoCodeRepository extends Neo4jRepository<PromoCode, String> {

    Optional<PromoCode> findByCode(String code);
}
