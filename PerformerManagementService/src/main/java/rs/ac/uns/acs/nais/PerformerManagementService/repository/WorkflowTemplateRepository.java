package rs.ac.uns.acs.nais.PerformerManagementService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.PerformerManagementService.model.WorkflowTemplate;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowTemplateRepository extends Neo4jRepository<WorkflowTemplate, String> {

    List<WorkflowTemplate> findByArchived(Boolean archived);

    Optional<WorkflowTemplate> findByName(String name);
}
