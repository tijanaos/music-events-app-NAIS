package rs.ac.uns.acs.nais.AdvertisingSearchService.service.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.AdvertisingSearchService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.request.AdTypeRequest;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.request.CreateAdSagaRequest;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.request.SagaAdPayloadRequest;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.SagaStartResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.exception.ConflictException;
import rs.ac.uns.acs.nais.AdvertisingSearchService.model.AdTypeDocument;
import rs.ac.uns.acs.nais.AdvertisingSearchService.repository.AdTypeRepository;
import rs.ac.uns.acs.nais.AdvertisingSearchService.saga.choreography.event.AdPayloadEvent;
import rs.ac.uns.acs.nais.AdvertisingSearchService.saga.choreography.event.AdTypeReadyEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdCreationSagaService {

    private final AdTypeRepository adTypeRepository;
    private final RabbitTemplate rabbitTemplate;

    public SagaStartResponse startSaga(CreateAdSagaRequest request) {
        validateAdMatchesType(request.getAdType(), request.getAd());

        String sagaId = UUID.randomUUID().toString();
        AdTypeResolution resolution = resolveAdType(request.getAdType());

        log.info("[CHOREOGRAPHY] sagaId={} resolved adTypeId={} createdInSaga={} for oglasId={}",
                sagaId, resolution.adType().getId(), resolution.createdInSaga(), request.getAd().getOglasId());

        AdTypeReadyEvent event = AdTypeReadyEvent.builder()
                .sagaId(sagaId)
                .adTypeId(resolution.adType().getId())
                .adTypeCreatedInSaga(resolution.createdInSaga())
                .ad(mapAd(request.getAd(), resolution.adType().getId()))
                .build();

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CHOREOGRAPHY_EXCHANGE,
                    RabbitMQConfig.AD_TYPE_READY_KEY,
                    event
            );
        } catch (Exception ex) {
            if (resolution.createdInSaga()) {
                adTypeRepository.deleteById(resolution.adType().getId());
            }
            throw new IllegalStateException("Nije uspelo slanje saga događaja za kreiranje oglasa.", ex);
        }

        return SagaStartResponse.builder()
                .sagaId(sagaId)
                .adTypeId(resolution.adType().getId())
                .adTypeCreated(resolution.createdInSaga())
                .status("STARTED")
                .message("Saga je pokrenuta. Elasticsearch je spreman, Milvus čeka događaj za kreiranje oglasa.")
                .build();
    }

    private void validateAdMatchesType(AdTypeRequest adType, SagaAdPayloadRequest ad) {
        boolean contentTypeCompatible =
                ("text".equalsIgnoreCase(adType.getContentType()) && "tekstualni".equalsIgnoreCase(ad.getTipOglasa()))
                        || ("image".equalsIgnoreCase(adType.getContentType()) && "vizuelni".equalsIgnoreCase(ad.getTipOglasa()))
                        || ("video".equalsIgnoreCase(adType.getContentType()) && "vizuelni".equalsIgnoreCase(ad.getTipOglasa()));

        if (!contentTypeCompatible) {
            throw new ConflictException("Tip oglasa u Milvus servisu mora biti kompatibilan sa contentType vrednošću iz Elasticsearch-a.");
        }

        if (!adType.getCategory().equalsIgnoreCase(ad.getKategorija())) {
            throw new ConflictException("Kategorija oglasa mora da bude ista u oba servisa.");
        }
    }

    private AdTypeResolution resolveAdType(AdTypeRequest request) {
        if (request.getId() != null) {
            return adTypeRepository.findById(request.getId())
                    .map(existing -> {
                        ensureEquivalent(existing, request);
                        return new AdTypeResolution(existing, false);
                    })
                    .orElseGet(() -> new AdTypeResolution(adTypeRepository.save(mapToDocument(request, request.getId())), true));
        }

        List<AdTypeDocument> matches = adTypeRepository.findByNaturalKey(
                request.getName(), request.getCategory(), request.getContentType());

        if (!matches.isEmpty()) {
            AdTypeDocument existing = matches.get(0);
            ensureEquivalent(existing, request);
            return new AdTypeResolution(existing, false);
        }

        Long nextId = nextId();
        return new AdTypeResolution(adTypeRepository.save(mapToDocument(request, nextId)), true);
    }

    private void ensureEquivalent(AdTypeDocument existing, AdTypeRequest request) {
        boolean same =
                existing.getName().equalsIgnoreCase(request.getName())
                        && existing.getDescription().equalsIgnoreCase(request.getDescription())
                        && existing.getContentType().equalsIgnoreCase(request.getContentType())
                        && existing.getCategory().equalsIgnoreCase(request.getCategory())
                        && existing.getTargetChannel().equalsIgnoreCase(request.getTargetChannel())
                        && existing.getIsActive().equals(request.getIsActive())
                        && existing.getRequiresApproval().equals(request.getRequiresApproval())
                        && existing.getAverageDurationDays().equals(request.getAverageDurationDays());

        if (!same) {
            throw new ConflictException("Vrsta oglasa već postoji, ali sa drugačijim podacima. Pošalji postojeći zapis ili novi ID.");
        }
    }

    private Long nextId() {
        return adTypeRepository.findTopByOrderByIdDesc()
                .map(AdTypeDocument::getId)
                .map(id -> id + 1)
                .orElse(1L);
    }

    private AdTypeDocument mapToDocument(AdTypeRequest request, Long id) {
        return AdTypeDocument.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .contentType(request.getContentType())
                .category(request.getCategory())
                .targetChannel(request.getTargetChannel())
                .isActive(request.getIsActive())
                .requiresApproval(request.getRequiresApproval())
                .averageDurationDays(request.getAverageDurationDays())
                .createdAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDate.now())
                .build();
    }

    private AdPayloadEvent mapAd(SagaAdPayloadRequest ad, Long adTypeId) {
        return AdPayloadEvent.builder()
                .oglasId(ad.getOglasId())
                .adTypeId(adTypeId)
                .naziv(ad.getNaziv())
                .opis(ad.getOpis())
                .tipOglasa(ad.getTipOglasa())
                .contentUrl(ad.getContentUrl())
                .status(ad.getStatus())
                .kategorija(ad.getKategorija())
                .datumKreiranja(ad.getDatumKreiranja())
                .datumPoslednjeIzmene(ad.getDatumPoslednjeIzmene())
                .kampanjaId(ad.getKampanjaId())
                .build();
    }

    private record AdTypeResolution(AdTypeDocument adType, boolean createdInSaga) {
    }
}
