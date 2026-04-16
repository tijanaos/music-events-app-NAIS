package rs.ac.uns.acs.nais.GraphDatabaseService.service;

import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ResourceDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Resource;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ResourceType;

import java.util.List;

public interface IResourceService {

    List<Resource> findAll();

    Resource findById(String id);

    Resource create(ResourceDTO dto);

    Resource update(String id, ResourceDTO dto);

    void delete(String id);

    List<Resource> findByType(ResourceType type);

    List<Resource> findByPortable(Boolean portable);
}
