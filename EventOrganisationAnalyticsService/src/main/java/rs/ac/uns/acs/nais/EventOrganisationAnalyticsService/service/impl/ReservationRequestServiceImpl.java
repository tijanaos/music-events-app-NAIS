package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request.ReservationRequestDto;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request.ZahtevaniResursItemRequest;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.response.ReservationRequestResponse;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.exception.ResourceNotFoundException;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ReservationRequestDocument;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.model.ZahtevaniResursItem;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.repository.ReservationRequestRepository;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service.ReservationRequestService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ReservationRequestServiceImpl implements ReservationRequestService {

    private final ReservationRequestRepository repository;

    @Override
    public ReservationRequestResponse create(ReservationRequestDto dto) {
        ReservationRequestDocument doc = toDocument(dto);
        doc.setId(UUID.randomUUID().toString());
        return toResponse(repository.save(doc));
    }

    @Override
    public ReservationRequestResponse findById(String id) {
        return toResponse(getOrThrow(id));
    }

    @Override
    public List<ReservationRequestResponse> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationRequestResponse> findByBinaId(String binaId) {
        return repository.findByBinaId(binaId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationRequestResponse> findByStatus(String status) {
        return repository.findByStatusZahteva(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationRequestResponse> findByIzvodjacId(String izvodjacId) {
        return repository.findByIzvodjacId(izvodjacId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationRequestResponse> findWithTasks() {
        return repository.findByImaTaskoveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationRequestResponse update(String id, ReservationRequestDto dto) {
        ReservationRequestDocument existing = getOrThrow(id);
        ReservationRequestDocument updated = toDocument(dto);
        updated.setId(existing.getId());
        return toResponse(repository.save(updated));
    }

    @Override
    public void delete(String id) {
        getOrThrow(id);
        repository.deleteById(id);
    }

    private ReservationRequestDocument getOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zahtev za rezervaciju nije pronadjen: " + id));
    }

    private ReservationRequestDocument toDocument(ReservationRequestDto dto) {
        List<ZahtevaniResursItem> resursi = dto.getZahtevanihResursa() == null ? List.of() :
                dto.getZahtevanihResursa().stream()
                        .map(this::toResursItem)
                        .collect(Collectors.toList());

        return ReservationRequestDocument.builder()
                .statusZahteva(dto.getStatusZahteva())
                .datumSlanja(dto.getDatumSlanja())
                .datumAzuriranja(dto.getDatumAzuriranja())
                .napomena(dto.getNapomena())
                .binaId(dto.getBinaId())
                .nazivBine(dto.getNazivBine())
                .tipBine(dto.getTipBine())
                .kapacitetBine(dto.getKapacitetBine())
                .izvodjacId(dto.getIzvodjacId())
                .imeIzvodjaca(dto.getImeIzvodjaca())
                .prezimeIzvodjaca(dto.getPrezimeIzvodjaca())
                .zanr(dto.getZanr())
                .popularnost(dto.getPopularnost())
                .datumNastupa(dto.getDatumNastupa())
                .vremePocetka(dto.getVremePocetka())
                .vremeKraja(dto.getVremeKraja())
                .zahtevanihResursa(resursi)
                .imaTaskove(dto.getImaTaskove())
                .brojTaskova(dto.getBrojTaskova())
                .detaljiNastupa(dto.getDetaljiNastupa())
                .build();
    }

    private ZahtevaniResursItem toResursItem(ZahtevaniResursItemRequest req) {
        return ZahtevaniResursItem.builder()
                .nazivResursa(req.getNazivResursa())
                .tipResursa(req.getTipResursa())
                .zahtevanrKolicina(req.getZahtevanrKolicina())
                .postojiUSistemu(req.getPostojiUSistemu())
                .statusResursa(req.getStatusResursa())
                .razlogOdbijanja(req.getRazlogOdbijanja())
                .build();
    }

    private ReservationRequestResponse toResponse(ReservationRequestDocument doc) {
        return ReservationRequestResponse.builder()
                .id(doc.getId())
                .statusZahteva(doc.getStatusZahteva())
                .datumSlanja(doc.getDatumSlanja())
                .datumAzuriranja(doc.getDatumAzuriranja())
                .napomena(doc.getNapomena())
                .binaId(doc.getBinaId())
                .nazivBine(doc.getNazivBine())
                .tipBine(doc.getTipBine())
                .kapacitetBine(doc.getKapacitetBine())
                .izvodjacId(doc.getIzvodjacId())
                .imeIzvodjaca(doc.getImeIzvodjaca())
                .prezimeIzvodjaca(doc.getPrezimeIzvodjaca())
                .zanr(doc.getZanr())
                .popularnost(doc.getPopularnost())
                .datumNastupa(doc.getDatumNastupa())
                .vremePocetka(doc.getVremePocetka())
                .vremeKraja(doc.getVremeKraja())
                .zahtevanihResursa(doc.getZahtevanihResursa())
                .imaTaskove(doc.getImaTaskove())
                .brojTaskova(doc.getBrojTaskova())
                .detaljiNastupa(doc.getDetaljiNastupa())
                .build();
    }
}
