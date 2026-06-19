package rs.ac.uns.acs.nais.PerformerManagementService.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "negotiation.saga.choreography.exchange";

    public static final String CONCLUDED_QUEUE = "negotiation.concluded.queue";
    public static final String CONCLUDED_KEY = "negotiation.concluded";

    public static final String KARTE_FAILED_QUEUE = "performer.karte.failed.queue";
    public static final String KARTE_FAILED_KEY = "performer.karte.failed";

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
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue concludedQueue() {
        return QueueBuilder.durable(CONCLUDED_QUEUE).build();
    }

    @Bean
    public Queue karteFailedQueue() {
        return QueueBuilder.durable(KARTE_FAILED_QUEUE).build();
    }

    @Bean
    public Binding concludedBinding() {
        return BindingBuilder.bind(concludedQueue()).to(sagaExchange()).with(CONCLUDED_KEY);
    }

    @Bean
    public Binding karteFailedBinding() {
        return BindingBuilder.bind(karteFailedQueue()).to(sagaExchange()).with(KARTE_FAILED_KEY);
    }
}
