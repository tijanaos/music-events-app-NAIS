package rs.ac.uns.acs.nais.GraphDatabaseService.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ konfiguracija za EventOrganisationService (Neo4j strana sage).
 *
 * Definiše orkestracioni exchange, komandne i reply queue-ove koji se koriste
 * za distribuiranu transakciju kreiranja rezervacije:
 *
 *   EventOrganisationService (Neo4j)            EventOrganisationAnalyticsService (Elasticsearch)
 *   --------------------------------            ---------------------------------------------------
 *   SagaOrchestrator.startSaga()
 *     -> CreateReservationCommand   ------------------------------------------------\
 *                                                                                    v
 *   CommandListener.handleCreateReservationCommand()                       (ovaj servis je oba kraja
 *     -> kreira Reservation cvor u Neo4j                                    za prvi korak, jer
 *     -> salje ReservationCreatedReply  <-----------------------------------     rezervacija zivi ovde)
 *
 *   SagaOrchestrator.handleReservationCreatedReply() [uspeh]
 *     -> RecordResourceUsageCommand ------------------------------------------------>
 *                                                                  CommandListener (Analytics servis)
 *                                                                    -> upisuje ResourceUsageDocument(e) u ES
 *   SagaOrchestrator.handleResourceUsageRecordedReply()  <----------- -> salje ResourceUsageRecordedReply
 *     [uspeh] -> COMPLETED
 *     [neuspeh] -> DeleteReservationCommand (kompenzacija, ostaje u ovom servisu)
 *
 * RabbitMQ kreira queue-ove i exchange pri pokretanju ako vec ne postoje.
 * Iste konstante (nazivi) deklarisane su i u EventOrganisationAnalyticsService.
 */
@Configuration
public class RabbitMQConfig {

    // -------------------------------------------------------------------------
    // Exchange
    // -------------------------------------------------------------------------

    public static final String ORCHESTRATION_EXCHANGE = "reservation.saga.orchestration.exchange";

    // -------------------------------------------------------------------------
    // Korak 1: kreiranje rezervacije (Neo4j) -- komanda i reply
    // -------------------------------------------------------------------------

    public static final String CREATE_RESERVATION_CMD_QUEUE = "create.reservation.command.queue";
    public static final String CREATE_RESERVATION_CMD_KEY   = "create.reservation.command";

    public static final String RESERVATION_CREATED_REPLY_QUEUE = "reservation.created.reply.queue";
    public static final String RESERVATION_CREATED_REPLY_KEY   = "reservation.created.reply";

    // -------------------------------------------------------------------------
    // Korak 2: upis iskoriscenosti resursa (Elasticsearch) -- komanda i reply
    // -------------------------------------------------------------------------

    public static final String RECORD_RESOURCE_USAGE_CMD_QUEUE = "record.resource.usage.command.queue";
    public static final String RECORD_RESOURCE_USAGE_CMD_KEY   = "record.resource.usage.command";

    public static final String RESOURCE_USAGE_RECORDED_REPLY_QUEUE = "resource.usage.recorded.reply.queue";
    public static final String RESOURCE_USAGE_RECORDED_REPLY_KEY   = "resource.usage.recorded.reply";

    // -------------------------------------------------------------------------
    // Kompenzacija: brisanje rezervacije (Neo4j) -- komanda i reply
    // -------------------------------------------------------------------------

    public static final String DELETE_RESERVATION_CMD_QUEUE = "delete.reservation.command.queue";
    public static final String DELETE_RESERVATION_CMD_KEY   = "delete.reservation.command";

    public static final String RESERVATION_DELETED_REPLY_QUEUE = "reservation.deleted.reply.queue";
    public static final String RESERVATION_DELETED_REPLY_KEY   = "reservation.deleted.reply";

    // =========================================================================
    // Message converter i RabbitTemplate
    // =========================================================================

    /** Konvertuje Java objekte u JSON poruke i obrnuto, za slanje i prijem. */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    // =========================================================================
    // Orkestracioni exchange
    // =========================================================================

    /** DirectExchange za orkestraciju -- tacno poklapanje routing key-eva. */
    @Bean
    public DirectExchange orchestrationExchange() {
        return new DirectExchange(ORCHESTRATION_EXCHANGE);
    }

    // -- Queue-ovi --

    @Bean public Queue createReservationCmdQueue()      { return QueueBuilder.durable(CREATE_RESERVATION_CMD_QUEUE).build(); }
    @Bean public Queue reservationCreatedReplyQueue()   { return QueueBuilder.durable(RESERVATION_CREATED_REPLY_QUEUE).build(); }

    @Bean public Queue recordResourceUsageCmdQueue()    { return QueueBuilder.durable(RECORD_RESOURCE_USAGE_CMD_QUEUE).build(); }
    @Bean public Queue resourceUsageRecordedReplyQueue(){ return QueueBuilder.durable(RESOURCE_USAGE_RECORDED_REPLY_QUEUE).build(); }

    @Bean public Queue deleteReservationCmdQueue()      { return QueueBuilder.durable(DELETE_RESERVATION_CMD_QUEUE).build(); }
    @Bean public Queue reservationDeletedReplyQueue()   { return QueueBuilder.durable(RESERVATION_DELETED_REPLY_QUEUE).build(); }

    // -- Bindinzi --

    @Bean public Binding createReservationCmdBinding() {
        return BindingBuilder.bind(createReservationCmdQueue()).to(orchestrationExchange()).with(CREATE_RESERVATION_CMD_KEY);
    }
    @Bean public Binding reservationCreatedReplyBinding() {
        return BindingBuilder.bind(reservationCreatedReplyQueue()).to(orchestrationExchange()).with(RESERVATION_CREATED_REPLY_KEY);
    }

    @Bean public Binding recordResourceUsageCmdBinding() {
        return BindingBuilder.bind(recordResourceUsageCmdQueue()).to(orchestrationExchange()).with(RECORD_RESOURCE_USAGE_CMD_KEY);
    }
    @Bean public Binding resourceUsageRecordedReplyBinding() {
        return BindingBuilder.bind(resourceUsageRecordedReplyQueue()).to(orchestrationExchange()).with(RESOURCE_USAGE_RECORDED_REPLY_KEY);
    }

    @Bean public Binding deleteReservationCmdBinding() {
        return BindingBuilder.bind(deleteReservationCmdQueue()).to(orchestrationExchange()).with(DELETE_RESERVATION_CMD_KEY);
    }
    @Bean public Binding reservationDeletedReplyBinding() {
        return BindingBuilder.bind(reservationDeletedReplyQueue()).to(orchestrationExchange()).with(RESERVATION_DELETED_REPLY_KEY);
    }
}