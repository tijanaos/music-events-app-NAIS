package rs.ac.uns.acs.nais.AdvertisingSearchService.saga.choreography;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.AdvertisingSearchService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.AdvertisingSearchService.saga.choreography.event.AdCreatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdSagaCompletionListener {

    @RabbitListener(queues = RabbitMQConfig.AD_CREATED_QUEUE)
    public void handleAdCreated(AdCreatedEvent event) {
        log.info("[CHOREOGRAPHY][SUCCESS] sagaId={} adTypeId={} oglasId={} successfully committed across Elasticsearch and Milvus",
                event.getSagaId(), event.getAdTypeId(), event.getOglasId());
    }
}
