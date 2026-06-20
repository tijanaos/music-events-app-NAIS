package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.GraphDatabaseService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.RequiresResourceDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Reservation;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.relationship.RequiresResource;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.IReservationService;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command.CreateReservationCommand;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command.DeleteReservationCommand;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command.ResourceUsageEntry;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.reply.ReservationCreatedReply;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.reply.ReservationDeletedReply;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandListener {

    private final IReservationService reservationService;
    private final RabbitTemplate rabbitTemplate;

    // Kreiranje rezervacije
    @RabbitListener(queues = RabbitMQConfig.CREATE_RESERVATION_CMD_QUEUE)
    public void handleCreateReservationCommand(CreateReservationCommand cmd) {
        log.info("[ORCHESTRATION][Neo4j] Primljena CreateReservationCommand -- sagaId={}", cmd.getSagaId());

        ReservationCreatedReply reply;
        try {
            // Kreiranje rezervacije
            Reservation reservation = reservationService.create(cmd.getReservation());

            // Povezivanje rezervacije sa resursima
            if (cmd.getRequestedResources() != null) {
                for (RequiresResourceDTO resourceDto : cmd.getRequestedResources()) {
                    reservation = reservationService.addResource(reservation.getId(), resourceDto);
                }
            }

            // Kreiranje ResourseUsageEntry-ja
            List<ResourceUsageEntry> usageEntries = buildResourceUsageEntries(reservation);

            log.info("[ORCHESTRATION][Neo4j] sagaId={} -- Reservacija kreirana, id={}, resursa={}",
                    cmd.getSagaId(), reservation.getId(), usageEntries.size());

            reply = new ReservationCreatedReply(cmd.getSagaId(), true, null, reservation.getId(), usageEntries);

        } catch (Exception e) {
            log.error("[ORCHESTRATION][Neo4j] sagaId={} -- Greska pri kreiranju rezervacije: {}",
                    cmd.getSagaId(), e.getMessage(), e);
            reply = new ReservationCreatedReply(cmd.getSagaId(), false, e.getMessage(), null, List.of());
        }

        sendReply(RabbitMQConfig.RESERVATION_CREATED_REPLY_KEY, reply, cmd.getSagaId());
    }

    // Kompenzacija - brisanje rezervacije
    @RabbitListener(queues = RabbitMQConfig.DELETE_RESERVATION_CMD_QUEUE)
    public void handleDeleteReservationCommand(DeleteReservationCommand cmd) {
        log.info("[ORCHESTRATION][Neo4j] Primljena kompenzaciona operacija DeleteReservationCommand  -- sagaId={}, reservationId={}",
                cmd.getSagaId(), cmd.getReservationId());

        ReservationDeletedReply reply;
        try {
            reservationService.delete(cmd.getReservationId());
            log.info("[ORCHESTRATION][Neo4j] sagaId={} -- Reservacija id={} je obrisana",
                    cmd.getSagaId(), cmd.getReservationId());
            reply = new ReservationDeletedReply(cmd.getSagaId(), true, null);
        } catch (Exception e) {
            log.error("[ORCHESTRATION][Neo4j] sagaId={} -- Greska pri brisanju rezervacije - kompenzacija nije uspela : {}",
                    cmd.getSagaId(), e.getMessage(), e);
            reply = new ReservationDeletedReply(cmd.getSagaId(), false, e.getMessage());
        }

        sendReply(RabbitMQConfig.RESERVATION_DELETED_REPLY_KEY, reply, cmd.getSagaId());
    }

    // Helperi

    private List<ResourceUsageEntry> buildResourceUsageEntries(Reservation reservation) {
        List<ResourceUsageEntry> entries = new ArrayList<>();

        if (reservation.getResources() == null || reservation.getStage() == null || reservation.getTimeSlot() == null) {
            return entries;
        }

        String stageId = reservation.getStage().getStage().getId();
        String stageName = reservation.getStage().getStage().getName();
        String stageType = reservation.getStage().getStage().getType() != null
                ? reservation.getStage().getStage().getType().name() : null;

        String timeSlotId = reservation.getTimeSlot().getTimeSlot().getId();
        LocalDate slotDate = reservation.getTimeSlot().getReservationDate();
        Integer startHour = reservation.getTimeSlot().getTimeSlot().getStartTime() != null
                ? reservation.getTimeSlot().getTimeSlot().getStartTime().getHour() : null;
        Integer endHour = reservation.getTimeSlot().getTimeSlot().getEndTime() != null
                ? reservation.getTimeSlot().getTimeSlot().getEndTime().getHour() : null;

        for (RequiresResource req : reservation.getResources()) {
            boolean borrowed = req.getBorrowingSourceId() != null && !req.getBorrowingSourceId().isBlank();

            entries.add(ResourceUsageEntry.builder()
                    .resourceId(req.getResource().getId())
                    .resourceName(req.getResource().getName())
                    .resourceType(req.getResource().getType() != null ? req.getResource().getType().name() : null)
                    .portable(req.getResource().getPortable())
                    .allocatedQuantity(req.getRequestedQuantity())
                    .stageId(stageId)
                    .stageName(stageName)
                    .stageType(stageType)
                    .timeSlotId(timeSlotId)
                    .date(slotDate)
                    .startTime(startHour)
                    .endTime(endHour)
                    .borrowedFromStage(borrowed)
                    .borrowingStageName(borrowed ? req.getBorrowingSourceId() : null)
                    .build());
        }

        return entries;
    }

    private <T> void sendReply(String routingKey, T reply, String sagaId) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORCHESTRATION_EXCHANGE, routingKey, reply);
            log.info("[ORCHESTRATION][Neo4j] sagaId={} -- reply poslat na routing key '{}'", sagaId, routingKey);
        } catch (Exception e) {
            log.error("[ORCHESTRATION][Neo4j] sagaId={} -- Greska pri slanju reply-a: {}", sagaId, e.getMessage(), e);
        }
    }
}