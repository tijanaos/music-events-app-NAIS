package rs.ac.uns.acs.nais.PerformerManagementService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import rs.ac.uns.acs.nais.PerformerManagementService.dto.StateDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.model.State;

@Mapper(componentModel = "spring")
public interface StateMapper {
    State toEntity(StateDTO dto);
    StateDTO toDTO(State entity);
    void updateEntity(StateDTO dto, @MappingTarget State entity);
}
