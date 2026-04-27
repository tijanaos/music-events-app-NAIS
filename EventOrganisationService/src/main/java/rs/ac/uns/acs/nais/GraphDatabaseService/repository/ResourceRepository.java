package rs.ac.uns.acs.nais.GraphDatabaseService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Resource;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ResourceType;

import java.util.List;

@Repository
public interface ResourceRepository extends Neo4jRepository<Resource, String> {

    List<Resource> findByType(ResourceType type);

    List<Resource> findByPortable(Boolean portable);
}
