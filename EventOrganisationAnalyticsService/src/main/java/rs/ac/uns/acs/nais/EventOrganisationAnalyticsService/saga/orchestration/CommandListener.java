package rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.saga.orchestration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.dto.request.ResourceUsageDto;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.saga.orchestration.command.RecordResourceUsageCommand;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.saga.orchestration.command.ResourceUsageEntry;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.saga.orchestration.reply.ResourceUsageRecordedReply;
import rs.ac.uns.acs.nais.EventOrganisationAnalyticsService.service.ResourceUsageService;

/**
 * Command listener za orkestrisanu sagu na Elasticsearch strani
 * (EventOrganisationAnalyticsService) -- drugi korak sage kreiranja rezervacije.
 *
 * Obradjuje RecordResourceUsageCommand: za svaki ResourceUsageEntry iz komande
 * kreira po jedan ResourceUsageDocument u indeksu "resource-usage", a zatim
 * salje ResourceUsageRecordedReply orkestratoru.
 *
 * Ako upis bilo kog zapisa ne uspe, ceo korak se smatra neuspesnim i salje se
 * success=false, sto kod orkestratora pokrece kompenzaciju (brisanje
 * rezervacije u Neo4j).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommandListener {

    private final ResourceUsageService resourceUsageService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.RECORD_RESOURCE_USAGE_CMD_QUEUE)
    public void handleRecordResourceUsageCommand(RecordResourceUsageCommand cmd) {
        log.info("[ORCHESTRATION][ES] Primljena RecordResourceUsageCommand -- sagaId={}, reservationId={}, zapisa={}",
                cmd.getSagaId(), cmd.getReservationId(),
                cmd.getResourceUsageEntries() == null ? 0 : cmd.getResourceUsageEntries().size());

        ResourceUsageRecordedReply reply;
        try {
            int recorded = 0;

            if (cmd.getResourceUsageEntries() == null || cmd.getResourceUsageEntries().isEmpty()) {
                String reason = "Rezervacija " + cmd.getReservationId() + " nema trazenih resursa za upis u ES";
                log.error("[ORCHESTRATION][ES] sagaId={} -- {}", cmd.getSagaId(), reason);
                reply = new ResourceUsageRecordedReply(cmd.getSagaId(), false, reason, 0);
                sendReply(reply);
                return;
            }

            for (ResourceUsageEntry entry : cmd.getResourceUsageEntries()) {
                ResourceUsageDto dto = toDto(entry, cmd.getReservationId());
                resourceUsageService.create(dto);
                recorded++;
            }

            log.info("[ORCHESTRATION][ES] sagaId={} -- upisano {} ResourceUsageDocument zapisa", cmd.getSagaId(), recorded);
            reply = new ResourceUsageRecordedReply(cmd.getSagaId(), true, null, recorded);

        } catch (Exception e) {
            log.error("[ORCHESTRATION][ES] sagaId={} -- GRESKA pri upisu iskoriscenosti resursa: {}",
                    cmd.getSagaId(), e.getMessage(), e);
            reply = new ResourceUsageRecordedReply(cmd.getSagaId(), false, e.getMessage(), 0);
        }

        sendReply(reply);
    }

    private ResourceUsageDto toDto(ResourceUsageEntry entry, String reservationId) {
        return ResourceUsageDto.builder()
                .resourceId(entry.getResourceId())
                .resourceName(entry.getResourceName())
                .resourceType(entry.getResourceType())
                .portable(entry.getPortable())
                .allocatedQuantity(entry.getAllocatedQuantity())
                .stageId(entry.getStageId())
                .stageName(entry.getStageName())
                .stageType(entry.getStageType())
                .timeSlotId(entry.getTimeSlotId())
                .date(entry.getDate())
                .startTime(entry.getStartTime())
                .endTime(entry.getEndTime())
                .borrowedFromStage(entry.getBorrowedFromStage())
                .borrowingStageName(entry.getBorrowingStageName())
                .reservationId(reservationId)
                .build();
    }

    private void sendReply(ResourceUsageRecordedReply reply) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORCHESTRATION_EXCHANGE,
                    RabbitMQConfig.RESOURCE_USAGE_RECORDED_REPLY_KEY,
                    reply);
            log.info("[ORCHESTRATION][ES] sagaId={} -- ResourceUsageRecordedReply poslat (success={})",
                    reply.getSagaId(), reply.isSuccess());
        } catch (Exception e) {
            log.error("[ORCHESTRATION][ES] sagaId={} -- GRESKA pri slanju ResourceUsageRecordedReply: {}",
                    reply.getSagaId(), e.getMessage(), e);
        }
    }
}