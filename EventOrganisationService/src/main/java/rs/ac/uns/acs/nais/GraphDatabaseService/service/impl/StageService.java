package rs.ac.uns.acs.nais.GraphDatabaseService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.HasResourceDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.SharesResourceDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.StageDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.mapper.StageMapper;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Resource;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Stage;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.StageType;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.relationship.HasResource;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.relationship.SharesResource;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.ResourceRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.StageRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.IStageService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService implements IStageService {

    private final StageRepository stageRepository;
    private final ResourceRepository resourceRepository;
    private final StageMapper stageMapper;

    @Override
    public List<Stage> findAll() {
        return stageRepository.findAll();
    }

    @Override
    public Stage findById(String id) {
        return stageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stage not found with id: " + id));
    }

    @Override
    public Stage create(StageDTO dto) {
        Stage stage = stageMapper.toEntity(dto);
        return stageRepository.save(stage);
    }

    @Override
    public Stage update(String id, StageDTO dto) {
        Stage existing = findById(id);
        stageMapper.updateEntity(dto, existing);
        return stageRepository.save(existing);
    }

    @Override
    public void delete(String id) {
        findById(id);
        stageRepository.deleteById(id);
    }

    @Override
    public List<Stage> findByType(StageType type) {
        return stageRepository.findByType(type);
    }

    @Override
    public List<Stage> findByActive(Boolean active) {
        return stageRepository.findByActive(active);
    }

    @Override
    public Stage addResource(String stageId, HasResourceDTO dto) {
        Stage stage = findById(stageId);
        Resource resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found with id: " + dto.getResourceId()));

        List<HasResource> resources = stage.getResources() != null ? new ArrayList<>(stage.getResources()) : new ArrayList<>();
        resources.add(HasResource.builder()
                .quantity(dto.getQuantity())
                .availableQuantity(dto.getAvailableQuantity())
                .addedDate(dto.getAddedDate())
                .resource(resource)
                .build());
        stage.setResources(resources);
        return stageRepository.save(stage);
    }

    @Override
    public Stage updateResource(String stageId, String resourceId, HasResourceDTO dto) {
        Stage stage = findById(stageId);
        List<HasResource> resources = stage.getResources() != null ? new ArrayList<>(stage.getResources()) : new ArrayList<>();
        HasResource rel = resources.stream()
                .filter(r -> r.getResource().getId().equals(resourceId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource relation not found for resourceId: " + resourceId));

        rel.setQuantity(dto.getQuantity());
        rel.setAvailableQuantity(dto.getAvailableQuantity());
        rel.setAddedDate(dto.getAddedDate());
        stage.setResources(resources);
        return stageRepository.save(stage);
    }

    @Override
    public Stage removeResource(String stageId, String resourceId) {
        Stage stage = findById(stageId);
        List<HasResource> resources = stage.getResources() != null ? new ArrayList<>(stage.getResources()) : new ArrayList<>();
        boolean removed = resources.removeIf(r -> r.getResource().getId().equals(resourceId));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource relation not found for resourceId: " + resourceId);
        }
        stage.setResources(resources);
        return stageRepository.save(stage);
    }

    @Override
    public Stage addSharedResource(String stageId, SharesResourceDTO dto) {
        Stage stage = findById(stageId);
        Stage targetStage = findById(dto.getTargetStageId());

        List<SharesResource> shared = stage.getSharedResources() != null ? new ArrayList<>(stage.getSharedResources()) : new ArrayList<>();
        shared.add(SharesResource.builder()
                .resourceId(dto.getResourceId())
                .sharingType(dto.getSharingType())
                .dateFrom(dto.getDateFrom())
                .dateTo(dto.getDateTo())
                .stage(targetStage)
                .build());
        stage.setSharedResources(shared);
        return stageRepository.save(stage);
    }

    @Override
    public Stage removeSharedResource(String stageId, String targetStageId, String resourceId) {
        Stage stage = findById(stageId);
        List<SharesResource> shared = stage.getSharedResources() != null ? new ArrayList<>(stage.getSharedResources()) : new ArrayList<>();
        boolean removed = shared.removeIf(s -> s.getStage().getId().equals(targetStageId) && s.getResourceId().equals(resourceId));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Shared resource relation not found");
        }
        stage.setSharedResources(shared);
        return stageRepository.save(stage);
    }
}
