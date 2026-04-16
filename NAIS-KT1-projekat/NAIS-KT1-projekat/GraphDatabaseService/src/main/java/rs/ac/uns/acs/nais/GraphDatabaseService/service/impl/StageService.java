package rs.ac.uns.acs.nais.GraphDatabaseService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.StageDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.mapper.StageMapper;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Stage;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.StageType;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.StageRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.IStageService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService implements IStageService {

    private final StageRepository stageRepository;
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
}
