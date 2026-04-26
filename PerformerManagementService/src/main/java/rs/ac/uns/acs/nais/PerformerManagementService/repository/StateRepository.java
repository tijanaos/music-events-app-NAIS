package rs.ac.uns.acs.nais.PerformerManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.PerformerManagementService.model.State;

import java.util.List;

@Repository
public interface StateRepository extends Neo4jRepository<State, String> {

    List<State> findByIsInitial(Boolean isInitial);

    List<State> findByIsFinal(Boolean isFinal);
}
