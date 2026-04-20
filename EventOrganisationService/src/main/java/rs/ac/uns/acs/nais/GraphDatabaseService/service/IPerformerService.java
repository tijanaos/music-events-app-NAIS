package rs.ac.uns.acs.nais.GraphDatabaseService.service;

import rs.ac.uns.acs.nais.GraphDatabaseService.dto.PerformerDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Performer;

import java.util.List;

public interface IPerformerService {

    List<Performer> findAll();

    Performer findById(String id);

    Performer create(PerformerDTO dto);

    Performer update(String id, PerformerDTO dto);

    void delete(String id);

    List<Performer> findByGenre(String genre);

    List<Performer> findByCountryOfOrigin(String country);

    List<Performer> findByMinPopularity(Double minPopularity);
}
