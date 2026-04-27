package rs.ac.uns.acs.nais.PerformerManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.PerformerManagementService.model.Performer;

import java.util.List;

@Repository
public interface PerformerRepository extends Neo4jRepository<Performer, String> {
    List<Performer> findByGenre(String genre);
    List<Performer> findByCountryOfOrigin(String countryOfOrigin);
    List<Performer> findByArchived(Boolean archived);
    List<Performer> findByMemberCount(Integer memberCount);
}
