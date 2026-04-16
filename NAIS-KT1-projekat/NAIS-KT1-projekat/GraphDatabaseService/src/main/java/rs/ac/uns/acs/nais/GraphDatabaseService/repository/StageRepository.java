package rs.ac.uns.acs.nais.GraphDatabaseService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Stage;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.StageType;

import java.util.List;
import java.util.Optional;

@Repository
public interface StageRepository extends Neo4jRepository<Stage, String> {

    List<Stage> findByType(StageType type);

    List<Stage> findByActive(Boolean active);

    Optional<Stage> findByName(String name);
}
