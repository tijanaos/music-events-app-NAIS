package rs.ac.uns.acs.nais.GraphDatabaseService.service;

import rs.ac.uns.acs.nais.GraphDatabaseService.dto.HasResourceDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.SharesResourceDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.StageDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Stage;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.enums.StageType;

import java.util.List;

public interface IStageService {

    List<Stage> findAll();

    Stage findById(String id);

    Stage create(StageDTO dto);

    Stage update(String id, StageDTO dto);

    void delete(String id);

    List<Stage> findByType(StageType type);

    List<Stage> findByActive(Boolean active);

    Stage addResource(String stageId, HasResourceDTO dto);

    Stage updateResource(String stageId, String resourceId, HasResourceDTO dto);

    Stage removeResource(String stageId, String resourceId);

    Stage addSharedResource(String stageId, SharesResourceDTO dto);

    Stage removeSharedResource(String stageId, String targetStageId, String resourceId);
}
