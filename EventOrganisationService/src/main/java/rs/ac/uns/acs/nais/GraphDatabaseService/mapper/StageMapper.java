package rs.ac.uns.acs.nais.GraphDatabaseService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.StageDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Stage;

@Mapper(componentModel = "spring")
public interface StageMapper {

    Stage toEntity(StageDTO dto);

    StageDTO toDTO(Stage stage);

    void updateEntity(StageDTO dto, @MappingTarget Stage stage);
}
