package rs.ac.uns.acs.nais.GraphDatabaseService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ResourceDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Resource;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    Resource toEntity(ResourceDTO dto);

    ResourceDTO toDTO(Resource resource);

    void updateEntity(ResourceDTO dto, @MappingTarget Resource resource);
}
