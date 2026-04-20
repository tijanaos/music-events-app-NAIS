package rs.ac.uns.acs.nais.GraphDatabaseService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.PerformerDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.mapper.PerformerMapper;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Performer;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.PerformerRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.IPerformerService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformerService implements IPerformerService {

    private final PerformerRepository performerRepository;
    private final PerformerMapper performerMapper;

    @Override
    public List<Performer> findAll() {
        return performerRepository.findAll();
    }

    @Override
    public Performer findById(String id) {
        return performerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Performer not found with id: " + id));
    }

    @Override
    public Performer create(PerformerDTO dto) {
        return performerRepository.save(performerMapper.toEntity(dto));
    }

    @Override
    public Performer update(String id, PerformerDTO dto) {
        Performer existing = findById(id);
        performerMapper.updateEntity(dto, existing);
        return performerRepository.save(existing);
    }

    @Override
    public void delete(String id) {
        findById(id);
        performerRepository.deleteById(id);
    }

    @Override
    public List<Performer> findByGenre(String genre) {
        return performerRepository.findByGenre(genre);
    }

    @Override
    public List<Performer> findByCountryOfOrigin(String country) {
        return performerRepository.findByCountryOfOrigin(country);
    }

    @Override
    public List<Performer> findByMinPopularity(Double minPopularity) {
        return performerRepository.findByPopularityGreaterThanEqual(minPopularity);
    }
}
