package rs.ac.uns.acs.nais.PerformerManagementService.service;

import rs.ac.uns.acs.nais.PerformerManagementService.dto.WorkflowTemplateDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.model.WorkflowTemplate;

import java.util.List;

public interface IWorkflowTemplateService {
    List<WorkflowTemplate> findAll();
    WorkflowTemplate findById(String id);
    WorkflowTemplate create(WorkflowTemplateDTO dto);
    WorkflowTemplate update(String id, WorkflowTemplateDTO dto);
    WorkflowTemplate archive(String id);
    WorkflowTemplate addState(String templateId, String stateId, Integer orderIndex);
}
