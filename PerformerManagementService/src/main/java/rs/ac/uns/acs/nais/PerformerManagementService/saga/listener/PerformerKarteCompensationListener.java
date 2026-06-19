package rs.ac.uns.acs.nais.PerformerManagementService.saga.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.PerformerManagementService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.PerformerManagementService.repository.NegotiationRepository;
import rs.ac.uns.acs.nais.PerformerManagementService.saga.event.PerformerKarteFailedEvent;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PerformerKarteCompensationListener {

    private final NegotiationRepository negotiationRepository;

    @RabbitListener(queues = RabbitMQConfig.KARTE_FAILED_QUEUE)
    public void handleKarteFailed(PerformerKarteFailedEvent event) {
        log.warn("[SAGA][COMPENSATION] sagaId={} negotiationId={} reason={}",
                event.getSagaId(), event.getNegotiationId(), event.getReason());

        negotiationRepository.findById(event.getNegotiationId()).ifPresent(negotiation -> {
            negotiation.setConcludedAt(null);
            negotiation.setUpdatedAt(LocalDateTime.now());
            negotiationRepository.save(negotiation);
            log.info("[SAGA][COMPENSATION] Reverted concludedAt for negotiationId={}", event.getNegotiationId());
        });
    }
}
