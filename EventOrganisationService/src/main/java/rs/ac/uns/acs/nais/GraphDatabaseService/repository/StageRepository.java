package rs.ac.uns.acs.nais.GraphDatabaseService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.StageResourceSummary;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Stage;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.StageType;

import java.util.List;

@Repository
public interface StageRepository extends Neo4jRepository<Stage, String> {

    List<Stage> findByType(StageType type);

    List<Stage> findByActive(Boolean active);

    // Query 1: For each active stage, count assigned resources and sum available quantity
    @Query("MATCH (s:Stage)-[r:HAS_RESOURCE]->(res:Resource) " +
           "WHERE s.active = true " +
           "WITH s, count(res) AS totalResources, sum(r.availableQuantity) AS totalAvailableQuantity " +
           "RETURN s.id AS stageId, s.name AS stageName, s.type AS stageType, " +
           "totalResources, totalAvailableQuantity " +
           "ORDER BY totalResources DESC")
    List<StageResourceSummary> findStageResourceSummary();

}
