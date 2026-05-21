package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request.ResourceUsageDto;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ResourceUsageResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.exception.ResourceNotFoundException;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ResourceUsageDocument;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.repository.ResourceUsageRepository;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service.ResourceUsageService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ResourceUsageServiceImpl implements ResourceUsageService {

    private final ResourceUsageRepository repository;

    @Override
    public ResourceUsageResponse create(ResourceUsageDto dto) {
        ResourceUsageDocument doc = toDocument(dto);
        doc.setId(UUID.randomUUID().toString());
        return toResponse(repository.save(doc));
    }

    @Override
    public ResourceUsageResponse findById(String id) {
        return toResponse(getOrThrow(id));
    }

    @Override
    public List<ResourceUsageResponse> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceUsageResponse> findByBinaId(String binaId) {
        return repository.findByBinaId(binaId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceUsageResponse> findByResursId(String resursId) {
        return repository.findByResursId(resursId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceUsageResponse> findByTipResursa(String tipResursa) {
        return repository.findByTipResursa(tipResursa).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceUsageResponse> findByPeriod(LocalDate from, LocalDate to) {
        return repository.findByDatumBetween(from, to).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceUsageResponse> findPozajmice() {
        return repository.findByPozajmljenoSaBineTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceUsageResponse> findByRezervacijaId(String rezervacijaId) {
        return repository.findByRezervacijaId(rezervacijaId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ResourceUsageResponse update(String id, ResourceUsageDto dto) {
        ResourceUsageDocument existing = getOrThrow(id);
        ResourceUsageDocument updated = toDocument(dto);
        updated.setId(existing.getId());
        return toResponse(repository.save(updated));
    }

    @Override
    public void delete(String id) {
        getOrThrow(id);
        repository.deleteById(id);
    }

    private ResourceUsageDocument getOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Koriscenje resursa nije pronadjeno: " + id));
    }

    private ResourceUsageDocument toDocument(ResourceUsageDto dto) {
        return ResourceUsageDocument.builder()
                .resursId(dto.getResursId())
                .nazivResursa(dto.getNazivResursa())
                .tipResursa(dto.getTipResursa())
                .prenosiv(dto.getPrenosiv())
                .dodeljenaKolicina(dto.getDodeljenaKolicina())
                .binaId(dto.getBinaId())
                .nazivBine(dto.getNazivBine())
                .tipBine(dto.getTipBine())
                .terminId(dto.getTerminId())
                .datum(dto.getDatum())
                .vremePocetka(dto.getVremePocetka())
                .vremeKraja(dto.getVremeKraja())
                .pozajmljenoSaBine(dto.getPozajmljenoSaBine())
                .nazivBinePozajmice(dto.getNazivBinePozajmice())
                .rezervacijaId(dto.getRezervacijaId())
                .build();
    }

    private ResourceUsageResponse toResponse(ResourceUsageDocument doc) {
        return ResourceUsageResponse.builder()
                .id(doc.getId())
                .resursId(doc.getResursId())
                .nazivResursa(doc.getNazivResursa())
                .tipResursa(doc.getTipResursa())
                .prenosiv(doc.getPrenosiv())
                .dodeljenaKolicina(doc.getDodeljenaKolicina())
                .binaId(doc.getBinaId())
                .nazivBine(doc.getNazivBine())
                .tipBine(doc.getTipBine())
                .terminId(doc.getTerminId())
                .datum(doc.getDatum())
                .vremePocetka(doc.getVremePocetka())
                .vremeKraja(doc.getVremeKraja())
                .pozajmljenoSaBine(doc.getPozajmljenoSaBine())
                .nazivBinePozajmice(doc.getNazivBinePozajmice())
                .rezervacijaId(doc.getRezervacijaId())
                .build();
    }
}
