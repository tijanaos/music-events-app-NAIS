package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.GraphDatabaseService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.RequiresResourceDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ReservationDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command.CreateReservationCommand;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command.DeleteReservationCommand;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command.RecordResourceUsageCommand;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.reply.ReservationCreatedReply;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.reply.ReservationDeletedReply;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.reply.ResourceUsageRecordedReply;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SagaOrchestrator {

    // Mapa svih sagi
    private final ConcurrentHashMap<String, SagaInstance> sagaRegistry = new ConcurrentHashMap<>();

    private final RabbitTemplate rabbitTemplate;

    public SagaOrchestrator(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // Pokretanje sage
    public String startSaga(ReservationDTO reservation, List<RequiresResourceDTO> requestedResources) {
        String sagaId = UUID.randomUUID().toString();
        SagaInstance instance = new SagaInstance(sagaId, reservation);
        sagaRegistry.put(sagaId, instance);

        log.info("[ORCHESTRATION] Pokrenuta saga sagaId={} -- bina={}, termin={}, izvodjac={}",
                sagaId, reservation.getStageId(), reservation.getTimeSlotId(), reservation.getPerformerId());

        CreateReservationCommand cmd = new CreateReservationCommand(sagaId, reservation, requestedResources);
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORCHESTRATION_EXCHANGE,
                    RabbitMQConfig.CREATE_RESERVATION_CMD_KEY,
                    cmd);
            log.info("[ORCHESTRATION] sagaId={} -- CreateReservationCommand poslata", sagaId);
        } catch (Exception e) {
            log.error("[ORCHESTRATION] sagaId={} -- GRESKA pri slanju CreateReservationCommand: {}",
                    sagaId, e.getMessage(), e);
            instance.failWith(SagaState.FAILED, e.getMessage());
        }

        return sagaId;
    }

    @RabbitListener(queues = RabbitMQConfig.RESERVATION_CREATED_REPLY_QUEUE)
    public void handleReservationCreatedReply(ReservationCreatedReply reply) {
        log.info("[ORCHESTRATION] Primljen ReservationCreatedReply -- sagaId={}, success={}",
                reply.getSagaId(), reply.isSuccess());

        SagaInstance instance = sagaRegistry.get(reply.getSagaId());
        if (instance == null) {
            log.error("[ORCHESTRATION] Nepoznata saga sagaId={} -- reply ignorisan", reply.getSagaId());
            return;
        }

        if (reply.isSuccess()) {
            instance.setReservationId(reply.getReservationId());
            instance.setState(SagaState.RESERVATION_CREATED);
            log.info("[ORCHESTRATION] sagaId={} -> RESERVATION_CREATED (reservationId={}), saljem RecordResourceUsageCommand",
                    reply.getSagaId(), reply.getReservationId());

            RecordResourceUsageCommand cmd = new RecordResourceUsageCommand(
                    reply.getSagaId(), reply.getReservationId(), reply.getResourceUsageEntries());
            try {
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.ORCHESTRATION_EXCHANGE,
                        RabbitMQConfig.RECORD_RESOURCE_USAGE_CMD_KEY,
                        cmd);
                log.info("[ORCHESTRATION] sagaId={} -- RecordResourceUsageCommand poslata", reply.getSagaId());
            } catch (Exception e) {
                log.error("[ORCHESTRATION] sagaId={} -- GRESKA pri slanju RecordResourceUsageCommand: {}",
                        reply.getSagaId(), e.getMessage(), e);
                // Pokretanje kompenzacije: rezervacija je kreirana, ali ES komanda ne moze da se posalje
                triggerCompensation(instance);
            }

        } else {
            log.error("[ORCHESTRATION] sagaId={} -- Neo4j korak NIJE uspeo: {}",
                    reply.getSagaId(), reply.getErrorMessage());
            instance.failWith(SagaState.FAILED, reply.getErrorMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.RESOURCE_USAGE_RECORDED_REPLY_QUEUE)
    public void handleResourceUsageRecordedReply(ResourceUsageRecordedReply reply) {
        log.info("[ORCHESTRATION] Primljen ResourceUsageRecordedReply -- sagaId={}, success={}",
                reply.getSagaId(), reply.isSuccess());

        SagaInstance instance = sagaRegistry.get(reply.getSagaId());
        if (instance == null) {
            log.error("[ORCHESTRATION] Nepoznata saga sagaId={} -- reply ignorisan", reply.getSagaId());
            return;
        }

        if (reply.isSuccess()) {
            instance.setState(SagaState.USAGE_RECORDED);
            instance.setState(SagaState.COMPLETED);
            log.info("[ORCHESTRATION] sagaId={} -> COMPLETED -- distribuirana transakcija uspesno zavrsena ({} zapisa upisano)",
                    reply.getSagaId(), reply.getRecordedCount());
        } else {
            log.error("[ORCHESTRATION] sagaId={} -- ES korak NIJE uspeo: {}, pokrece se kompenzacija",
                    reply.getSagaId(), reply.getErrorMessage());
            instance.setErrorMessage(reply.getErrorMessage());
            triggerCompensation(instance);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.RESERVATION_DELETED_REPLY_QUEUE)
    public void handleReservationDeletedReply(ReservationDeletedReply reply) {
        log.info("[ORCHESTRATION] Primljen ReservationDeletedReply -- sagaId={}, success={}",
                reply.getSagaId(), reply.isSuccess());

        SagaInstance instance = sagaRegistry.get(reply.getSagaId());
        if (instance == null) {
            log.error("[ORCHESTRATION] Nepoznata saga sagaId={} -- reply ignorisan", reply.getSagaId());
            return;
        }

        if (reply.isSuccess()) {
            instance.setState(SagaState.COMPENSATED);
            log.warn("[ORCHESTRATION] sagaId={} -> COMPENSATED -- rezervacija obrisana, saga otkazana",
                    reply.getSagaId());
        } else {
            instance.failWith(SagaState.FAILED, "Kompenzacija nije uspela: " + reply.getSagaId());
            log.error("[ORCHESTRATION] sagaId={} -> FAILED -- kompenzacija NIJE uspela.",
                    reply.getSagaId());
        }
    }

    // Helperi
    private void triggerCompensation(SagaInstance instance) {
        instance.setState(SagaState.COMPENSATING);
        log.warn("[ORCHESTRATION] sagaId={} -> COMPENSATING -- saljem DeleteReservationCommand",
                instance.getSagaId());

        DeleteReservationCommand cmd = new DeleteReservationCommand(instance.getSagaId(), instance.getReservationId());
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORCHESTRATION_EXCHANGE,
                    RabbitMQConfig.DELETE_RESERVATION_CMD_KEY,
                    cmd);
            log.info("[ORCHESTRATION] sagaId={} -- DeleteReservationCommand poslata", instance.getSagaId());
        } catch (Exception e) {
            log.error("[ORCHESTRATION] sagaId={} -- GRESKA: DeleteReservationCommand nije mogla da se posalje: {}",
                    instance.getSagaId(), e.getMessage(), e);
            instance.setState(SagaState.FAILED);
        }
    }

    public SagaInstance getSagaStatus(String sagaId) {
        return sagaRegistry.get(sagaId);
    }

    public java.util.Collection<SagaInstance> getAllSagas() {
        return sagaRegistry.values();
    }
}