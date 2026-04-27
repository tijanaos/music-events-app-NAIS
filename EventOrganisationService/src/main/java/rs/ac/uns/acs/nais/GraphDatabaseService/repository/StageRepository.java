package rs.ac.uns.acs.nais.GraphDatabaseService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.StageAvailableResource;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.query.StageResourceSummary;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Stage;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.StageType;

import java.util.List;
import java.util.Optional;

@Repository
public interface StageRepository extends Neo4jRepository<Stage, String> {

    List<Stage> findByType(StageType type);

    List<Stage> findByActive(Boolean active);

    Optional<Stage> findByName(String name);

    // Query 1: For each active stage, count assigned resources and sum available quantity
    @Query("MATCH (s:Stage)-[r:HAS_RESOURCE]->(res:Resource) " +
           "WHERE s.active = true " +
           "WITH s, count(res) AS totalResources, sum(r.availableQuantity) AS totalAvailableQuantity " +
           "RETURN s.id AS stageId, s.name AS stageName, s.type AS stageType, " +
           "totalResources, totalAvailableQuantity " +
           "ORDER BY totalResources DESC")
    List<StageResourceSummary> findStageResourceSummary();

    // Query 5: For each stage, aggregate available quantity of a specific resource type
    @Query("MATCH (s:Stage)-[r:HAS_RESOURCE]->(res:Resource) " +
           "WHERE res.type = $resourceType AND r.availableQuantity >= $minQuantity " +
           "WITH s, count(res) AS resourceCount, sum(r.availableQuantity) AS totalAvailable " +
           "RETURN s.id AS stageId, s.name AS stageName, s.type AS stageType, " +
           "resourceCount, totalAvailable " +
           "ORDER BY totalAvailable DESC")
    List<StageAvailableResource> findStagesWithAvailableResource(
            @Param("minQuantity") Integer minQuantity,
            @Param("resourceType") String resourceType);
}
