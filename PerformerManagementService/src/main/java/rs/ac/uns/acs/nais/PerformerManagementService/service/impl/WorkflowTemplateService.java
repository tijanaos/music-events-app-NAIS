package rs.ac.uns.acs.nais.PerformerManagementService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.acs.nais.PerformerManagementService.dto.WorkflowTemplateDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.mapper.WorkflowTemplateMapper;
import rs.ac.uns.acs.nais.PerformerManagementService.model.State;
import rs.ac.uns.acs.nais.PerformerManagementService.model.WorkflowTemplate;
import rs.ac.uns.acs.nais.PerformerManagementService.model.relationship.ContainsState;
import rs.ac.uns.acs.nais.PerformerManagementService.repository.StateRepository;
import rs.ac.uns.acs.nais.PerformerManagementService.repository.WorkflowTemplateRepository;
import rs.ac.uns.acs.nais.PerformerManagementService.service.IWorkflowTemplateService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowTemplateService implements IWorkflowTemplateService {

    private final WorkflowTemplateRepository workflowTemplateRepository;
    private final StateRepository stateRepository;
    private final WorkflowTemplateMapper workflowTemplateMapper;

    @Override
    public List<WorkflowTemplate> findAll() {
        return workflowTemplateRepository.findAll();
    }

    @Override
    public WorkflowTemplate findById(String id) {
        return workflowTemplateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WorkflowTemplate not found with id: " + id));
    }

    @Override
    public WorkflowTemplate create(WorkflowTemplateDTO dto) {
        WorkflowTemplate template = workflowTemplateMapper.toEntity(dto);
        template.setArchived(false);
        template.setStates(new ArrayList<>());
        return workflowTemplateRepository.save(template);
    }

    @Override
    public WorkflowTemplate update(String id, WorkflowTemplateDTO dto) {
        WorkflowTemplate existing = findById(id);
        workflowTemplateMapper.updateEntity(dto, existing);
        return workflowTemplateRepository.save(existing);
    }

    @Override
    public WorkflowTemplate archive(String id) {
        WorkflowTemplate existing = findById(id);
        existing.setArchived(true);
        return workflowTemplateRepository.save(existing);
    }

    @Override
    public WorkflowTemplate addState(String templateId, String stateId, Integer orderIndex) {
        WorkflowTemplate template = findById(templateId);
        State state = stateRepository.findById(stateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "State not found with id: " + stateId));

        ContainsState containsState = ContainsState.builder()
                .orderIndex(orderIndex)
                .state(state)
                .build();

        if (template.getStates() == null) {
            template.setStates(new ArrayList<>());
        }
        template.getStates().add(containsState);
        return workflowTemplateRepository.save(template);
    }
}
