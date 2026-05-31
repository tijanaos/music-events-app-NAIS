package rs.ac.uns.acs.nais.AdvertisingSearchService.saga.choreography;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.AdvertisingSearchService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.AdvertisingSearchService.repository.AdTypeRepository;
import rs.ac.uns.acs.nais.AdvertisingSearchService.saga.choreography.event.AdCreationFailedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdSagaCompensationListener {

    private final AdTypeRepository adTypeRepository;

    @RabbitListener(queues = RabbitMQConfig.AD_CREATION_FAILED_QUEUE)
    public void handleAdCreationFailed(AdCreationFailedEvent event) {
        log.warn("[CHOREOGRAPHY][COMPENSATION] sagaId={} oglasId={} reason={}",
                event.getSagaId(), event.getOglasId(), event.getReason());

        if (!event.isAdTypeCreatedInSaga()) {
            log.info("[CHOREOGRAPHY][COMPENSATION] sagaId={} ad type existed before saga, skipping Elasticsearch delete",
                    event.getSagaId());
            return;
        }

        if (event.getAdTypeId() == null) {
            log.warn("[CHOREOGRAPHY][COMPENSATION] sagaId={} missing adTypeId, cannot compensate", event.getSagaId());
            return;
        }

        if (adTypeRepository.existsById(event.getAdTypeId())) {
            adTypeRepository.deleteById(event.getAdTypeId());
            log.info("[CHOREOGRAPHY][COMPENSATION] sagaId={} removed adTypeId={} from Elasticsearch",
                    event.getSagaId(), event.getAdTypeId());
            return;
        }

        log.info("[CHOREOGRAPHY][COMPENSATION] sagaId={} adTypeId={} already absent, compensation is idempotent",
                event.getSagaId(), event.getAdTypeId());
    }
}
