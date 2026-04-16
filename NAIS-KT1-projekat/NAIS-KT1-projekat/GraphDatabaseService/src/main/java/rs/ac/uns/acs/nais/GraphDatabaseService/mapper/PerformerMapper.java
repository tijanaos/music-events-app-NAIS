package rs.ac.uns.acs.nais.GraphDatabaseService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.PerformerDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Performer;

@Mapper(componentModel = "spring")
public interface PerformerMapper {

    Performer toEntity(PerformerDTO dto);

    PerformerDTO toDTO(Performer performer);

    void updateEntity(PerformerDTO dto, @MappingTarget Performer performer);
}
