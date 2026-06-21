package rs.ac.uns.acs.nais.PerformerManagementService.saga.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.PerformerManagementService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.PerformerManagementService.repository.OfferRepository;
import rs.ac.uns.acs.nais.PerformerManagementService.saga.event.EscalationPriceFailedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class EscalationCompensationListener {

    private final OfferRepository offerRepository;

    @RabbitListener(queues = RabbitMQConfig.ESCALATION_FAILED_QUEUE)
    public void handleEscalationFailed(EscalationPriceFailedEvent event) {
        log.warn("[SAGA][COMPENSATION] sagaId={} negotiationId={} reason={}",
                event.getSagaId(), event.getNegotiationId(), event.getReason());

        offerRepository.findById(event.getOfferId()).ifPresent(offer -> {
            offer.setPrice(event.getStaraCena());
            offerRepository.save(offer);
            log.info("[SAGA][COMPENSATION] Reverted offer.price to {} for offerId={}",
                    event.getStaraCena(), event.getOfferId());
        });
    }
}