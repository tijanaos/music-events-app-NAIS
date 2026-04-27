package rs.ac.uns.acs.nais.PerformerManagementService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import rs.ac.uns.acs.nais.PerformerManagementService.dto.PerformerDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.model.Performer;

@Mapper(componentModel = "spring")
public interface PerformerMapper {
    Performer toEntity(PerformerDTO dto);
    PerformerDTO toDTO(Performer entity);
    void updateEntity(PerformerDTO dto, @MappingTarget Performer entity);
}
