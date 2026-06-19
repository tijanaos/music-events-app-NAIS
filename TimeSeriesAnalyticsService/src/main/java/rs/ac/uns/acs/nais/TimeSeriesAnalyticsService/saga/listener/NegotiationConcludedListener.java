package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.saga.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.saga.event.NegotiationConcludedEvent;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.saga.event.PerformerKarteFailedEvent;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.service.PerformerKarteService;

@Slf4j
@Component
@RequiredArgsConstructor
public class NegotiationConcludedListener {

    private final PerformerKarteService performerKarteService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.CONCLUDED_QUEUE)
    public void handleNegotiationConcluded(NegotiationConcludedEvent event) {
        log.info("[SAGA][STEP2] sagaId={} negotiationId={} performerId={}",
                event.getSagaId(), event.getNegotiationId(), event.getPerformerId());

        try {
            performerKarteService.writeAllocation(event);
            log.info("[SAGA][SUCCESS] sagaId={} allocated {} tickets for performer={}",
                    event.getSagaId(), 5, event.getPerformerName());
        } catch (Exception ex) {
            log.error("[SAGA][STEP2 FAILED] sagaId={} reason={}", event.getSagaId(), ex.getMessage());
            PerformerKarteFailedEvent failedEvent = new PerformerKarteFailedEvent(
                    event.getSagaId(), event.getNegotiationId(), ex.getMessage());
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.KARTE_FAILED_KEY, failedEvent);
        }
    }
}
