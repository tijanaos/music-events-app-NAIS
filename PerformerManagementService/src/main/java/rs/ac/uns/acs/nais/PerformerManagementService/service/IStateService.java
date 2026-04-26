package rs.ac.uns.acs.nais.PerformerManagementService.service;

import rs.ac.uns.acs.nais.PerformerManagementService.dto.StateDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.model.State;

import java.util.List;

public interface IStateService {
    List<State> findAll();
    State findById(String id);
    State create(StateDTO dto);
    State update(String id, StateDTO dto);
    void delete(String id);
}
