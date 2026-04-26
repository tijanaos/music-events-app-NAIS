package rs.ac.uns.acs.nais.PerformerManagementService.service;

import rs.ac.uns.acs.nais.PerformerManagementService.dto.PerformerDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.model.Performer;

import java.util.List;

public interface IPerformerService {
    List<Performer> findAll();
    Performer findById(String id);
    Performer create(PerformerDTO dto);
    Performer update(String id, PerformerDTO dto);
    Performer archive(String id);
    List<Performer> findByGenre(String genre);
    List<Performer> findByCountry(String country);
    List<Performer> findByMemberCount(Integer memberCount);
}
