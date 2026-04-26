package rs.ac.uns.acs.nais.PerformerManagementService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import rs.ac.uns.acs.nais.PerformerManagementService.dto.WorkflowTemplateDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.model.WorkflowTemplate;

@Mapper(componentModel = "spring")
public interface WorkflowTemplateMapper {
    WorkflowTemplate toEntity(WorkflowTemplateDTO dto);
    WorkflowTemplateDTO toDTO(WorkflowTemplate entity);
    void updateEntity(WorkflowTemplateDTO dto, @MappingTarget WorkflowTemplate entity);
}
