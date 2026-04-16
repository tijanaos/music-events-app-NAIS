package rs.ac.uns.acs.nais.GraphDatabaseService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ResourceDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.mapper.ResourceMapper;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Resource;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.ResourceType;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.ResourceRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.IResourceService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService implements IResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    @Override
    public List<Resource> findAll() {
        return resourceRepository.findAll();
    }

    @Override
    public Resource findById(String id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found with id: " + id));
    }

    @Override
    public Resource create(ResourceDTO dto) {
        Resource resource = resourceMapper.toEntity(dto);
        return resourceRepository.save(resource);
    }

    @Override
    public Resource update(String id, ResourceDTO dto) {
        Resource existing = findById(id);
        resourceMapper.updateEntity(dto, existing);
        return resourceRepository.save(existing);
    }

    @Override
    public void delete(String id) {
        findById(id);
        resourceRepository.deleteById(id);
    }

    @Override
    public List<Resource> findByType(ResourceType type) {
        return resourceRepository.findByType(type);
    }

    @Override
    public List<Resource> findByPortable(Boolean portable) {
        return resourceRepository.findByPortable(portable);
    }
}
