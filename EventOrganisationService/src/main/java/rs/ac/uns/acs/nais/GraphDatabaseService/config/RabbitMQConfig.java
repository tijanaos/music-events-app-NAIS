package rs.ac.uns.acs.nais.GraphDatabaseService.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORCHESTRATION_EXCHANGE = "reservation.saga.orchestration.exchange";

    // Kreiranje rezervacije (komanda i reply)
    public static final String CREATE_RESERVATION_CMD_QUEUE = "create.reservation.command.queue";
    public static final String CREATE_RESERVATION_CMD_KEY   = "create.reservation.command";

    public static final String RESERVATION_CREATED_REPLY_QUEUE = "reservation.created.reply.queue";
    public static final String RESERVATION_CREATED_REPLY_KEY   = "reservation.created.reply";

    // Upis iskoriscenosti resursa (komanda i reply)
    public static final String RECORD_RESOURCE_USAGE_CMD_QUEUE = "record.resource.usage.command.queue";
    public static final String RECORD_RESOURCE_USAGE_CMD_KEY   = "record.resource.usage.command";

    public static final String RESOURCE_USAGE_RECORDED_REPLY_QUEUE = "resource.usage.recorded.reply.queue";
    public static final String RESOURCE_USAGE_RECORDED_REPLY_KEY   = "resource.usage.recorded.reply";

    // Brisanje rezervacije (komanda i reply)
    public static final String DELETE_RESERVATION_CMD_QUEUE = "delete.reservation.command.queue";
    public static final String DELETE_RESERVATION_CMD_KEY   = "delete.reservation.command";

    public static final String RESERVATION_DELETED_REPLY_QUEUE = "reservation.deleted.reply.queue";
    public static final String RESERVATION_DELETED_REPLY_KEY   = "reservation.deleted.reply";

    // Java-JSON konverter i RabbitTemplate
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(mapper);
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

    @Bean
    public DirectExchange orchestrationExchange() {
        return new DirectExchange(ORCHESTRATION_EXCHANGE);
    }

    // Queues

    @Bean public Queue createReservationCmdQueue()      { return QueueBuilder.durable(CREATE_RESERVATION_CMD_QUEUE).build(); }
    @Bean public Queue reservationCreatedReplyQueue()   { return QueueBuilder.durable(RESERVATION_CREATED_REPLY_QUEUE).build(); }

    @Bean public Queue recordResourceUsageCmdQueue()    { return QueueBuilder.durable(RECORD_RESOURCE_USAGE_CMD_QUEUE).build(); }
    @Bean public Queue resourceUsageRecordedReplyQueue(){ return QueueBuilder.durable(RESOURCE_USAGE_RECORDED_REPLY_QUEUE).build(); }

    @Bean public Queue deleteReservationCmdQueue()      { return QueueBuilder.durable(DELETE_RESERVATION_CMD_QUEUE).build(); }
    @Bean public Queue reservationDeletedReplyQueue()   { return QueueBuilder.durable(RESERVATION_DELETED_REPLY_QUEUE).build(); }

    // Bindings

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