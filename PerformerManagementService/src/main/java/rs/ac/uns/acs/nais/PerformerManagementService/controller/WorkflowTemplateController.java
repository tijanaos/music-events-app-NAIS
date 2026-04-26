package rs.ac.uns.acs.nais.PerformerManagementService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.PerformerManagementService.dto.WorkflowTemplateDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.model.WorkflowTemplate;
import rs.ac.uns.acs.nais.PerformerManagementService.service.IWorkflowTemplateService;

import java.util.List;

@RestController
@RequestMapping("/api/workflow-templates")
@RequiredArgsConstructor
@Tag(name = "WorkflowTemplate", description = "Workflow template management endpoints")
public class WorkflowTemplateController {

    private final IWorkflowTemplateService workflowTemplateService;

    @GetMapping
    @Operation(summary = "Get all workflow templates")
    public ResponseEntity<List<WorkflowTemplate>> getAll() {
        return ResponseEntity.ok(workflowTemplateService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get workflow template by ID")
    public ResponseEntity<WorkflowTemplate> getById(@PathVariable String id) {
        return ResponseEntity.ok(workflowTemplateService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new workflow template")
    public ResponseEntity<WorkflowTemplate> create(@RequestBody WorkflowTemplateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowTemplateService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a workflow template")
    public ResponseEntity<WorkflowTemplate> update(@PathVariable String id, @RequestBody WorkflowTemplateDTO dto) {
        return ResponseEntity.ok(workflowTemplateService.update(id, dto));
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive a workflow template")
    public ResponseEntity<WorkflowTemplate> archive(@PathVariable String id) {
        return ResponseEntity.ok(workflowTemplateService.archive(id));
    }

    @PostMapping("/{templateId}/states/{stateId}")
    @Operation(summary = "Add a state to a workflow template")
    public ResponseEntity<WorkflowTemplate> addState(
            @PathVariable String templateId,
            @PathVariable String stateId,
            @RequestParam Integer orderIndex) {
        return ResponseEntity.ok(workflowTemplateService.addState(templateId, stateId, orderIndex));
    }
}
