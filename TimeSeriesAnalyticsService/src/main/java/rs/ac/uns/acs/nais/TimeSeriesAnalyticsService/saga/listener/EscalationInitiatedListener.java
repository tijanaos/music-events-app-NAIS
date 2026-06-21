package rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.saga.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.dto.request.PromenaCeneRequest;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.saga.event.EscalationInitiatedEvent;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.saga.event.EscalationPriceFailedEvent;
import rs.ac.uns.acs.nais.TimeSeriesAnalyticsService.service.PromenaCeneService;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class EscalationInitiatedListener {

    private final PromenaCeneService promenaCeneService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.escalation.fail-for-demo:false}")
    private boolean failForDemo;

    @RabbitListener(queues = RabbitMQConfig.ESCALATION_INITIATED_QUEUE)
    public void handleEscalationInitiated(EscalationInitiatedEvent event) {
        log.info("[SAGA][ESCALATION][RECEIVED] sagaId={} negotiationId={}",
                event.getSagaId(), event.getNegotiationId());

        try {
            if (failForDemo) {
                throw new RuntimeException("Simulirana greska za demo");
            }

            PromenaCeneRequest request = new PromenaCeneRequest();
            request.setFestivalId(event.getNegotiationId());
            request.setNazivFestivala(event.getPerformerName());
            request.setTipKarte("NEGOTIATION_ESCALATION");
            request.setRazlog("Automatska eskalacija pregovora");
            request.setStaraCena(event.getStaraCena());
            request.setNovaCena(event.getNovaCena());
            request.setTime(Instant.now());

            promenaCeneService.create(request);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ESCALATION_RECORDED_KEY,
                    event);
            log.info("[SAGA][ESCALATION][RECORDED] sagaId={} staraCena={} novaCena={}",
                    event.getSagaId(), event.getStaraCena(), event.getNovaCena());

        } catch (Exception ex) {
            log.error("[SAGA][ESCALATION][FAILED] sagaId={} reason={}", event.getSagaId(), ex.getMessage());

            EscalationPriceFailedEvent failedEvent = new EscalationPriceFailedEvent(
                    event.getSagaId(),
                    event.getNegotiationId(),
                    event.getOfferId(),
                    event.getStaraCena(),
                    ex.getMessage()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ESCALATION_FAILED_KEY,
                    failedEvent);
        }
    }
}