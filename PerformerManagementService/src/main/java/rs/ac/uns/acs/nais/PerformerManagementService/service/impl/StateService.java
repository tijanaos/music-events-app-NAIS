package rs.ac.uns.acs.nais.PerformerManagementService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.acs.nais.PerformerManagementService.dto.StateDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.mapper.StateMapper;
import rs.ac.uns.acs.nais.PerformerManagementService.model.State;
import rs.ac.uns.acs.nais.PerformerManagementService.repository.StateRepository;
import rs.ac.uns.acs.nais.PerformerManagementService.service.IStateService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StateService implements IStateService {

    private final StateRepository stateRepository;
    private final StateMapper stateMapper;

    @Override
    public List<State> findAll() {
        return stateRepository.findAll();
    }

    @Override
    public State findById(String id) {
        return stateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "State not found with id: " + id));
    }

    @Override
    public State create(StateDTO dto) {
        return stateRepository.save(stateMapper.toEntity(dto));
    }

    @Override
    public State update(String id, StateDTO dto) {
        State existing = findById(id);
        stateMapper.updateEntity(dto, existing);
        return stateRepository.save(existing);
    }

    @Override
    public void delete(String id) {
        findById(id);
        stateRepository.deleteById(id);
    }
}
