package rs.ac.uns.acs.nais.PerformerManagementService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.acs.nais.PerformerManagementService.dto.PerformerDTO;
import rs.ac.uns.acs.nais.PerformerManagementService.mapper.PerformerMapper;
import rs.ac.uns.acs.nais.PerformerManagementService.model.Performer;
import rs.ac.uns.acs.nais.PerformerManagementService.repository.PerformerRepository;
import rs.ac.uns.acs.nais.PerformerManagementService.service.IPerformerService;

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
        Performer performer = performerMapper.toEntity(dto);
        performer.setArchived(false);
        return performerRepository.save(performer);
    }

    @Override
    public Performer update(String id, PerformerDTO dto) {
        Performer existing = findById(id);
        performerMapper.updateEntity(dto, existing);
        return performerRepository.save(existing);
    }

    @Override
    public Performer archive(String id) {
        Performer existing = findById(id);
        existing.setArchived(true);
        return performerRepository.save(existing);
    }

    @Override
    public List<Performer> findByGenre(String genre) {
        return performerRepository.findByGenre(genre);
    }

    @Override
    public List<Performer> findByCountry(String country) {
        return performerRepository.findByCountryOfOrigin(country);
    }

    @Override
    public List<Performer> findByMemberCount(Integer memberCount) {
        return performerRepository.findByMemberCount(memberCount);
    }
}
