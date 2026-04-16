package rs.ac.uns.acs.nais.GraphDatabaseService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Performer;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerformerRepository extends Neo4jRepository<Performer, String> {

    List<Performer> findByGenre(String genre);

    List<Performer> findByCountryOfOrigin(String countryOfOrigin);

    Optional<Performer> findByName(String name);

    List<Performer> findByPopularityGreaterThanEqual(Double minPopularity);
}
