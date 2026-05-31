package rs.ac.uns.acs.nais.AdvertisingSearchService.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CHOREOGRAPHY_EXCHANGE = "ad.saga.choreography.exchange";

    public static final String AD_TYPE_READY_QUEUE = "ad.type.ready.queue";
    public static final String AD_TYPE_READY_KEY = "ad.type.ready";

    public static final String AD_CREATED_QUEUE = "ad.created.queue";
    public static final String AD_CREATED_KEY = "ad.created";

    public static final String AD_CREATION_FAILED_QUEUE = "ad.creation.failed.queue";
    public static final String AD_CREATION_FAILED_KEY = "ad.creation.failed";

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
    public TopicExchange choreographyExchange() {
        return new TopicExchange(CHOREOGRAPHY_EXCHANGE);
    }

    @Bean
    public Queue adTypeReadyQueue() {
        return QueueBuilder.durable(AD_TYPE_READY_QUEUE).build();
    }

    @Bean
    public Queue adCreatedQueue() {
        return QueueBuilder.durable(AD_CREATED_QUEUE).build();
    }

    @Bean
    public Queue adCreationFailedQueue() {
        return QueueBuilder.durable(AD_CREATION_FAILED_QUEUE).build();
    }

    @Bean
    public Binding adTypeReadyBinding() {
        return BindingBuilder.bind(adTypeReadyQueue()).to(choreographyExchange()).with(AD_TYPE_READY_KEY);
    }

    @Bean
    public Binding adCreatedBinding() {
        return BindingBuilder.bind(adCreatedQueue()).to(choreographyExchange()).with(AD_CREATED_KEY);
    }

    @Bean
    public Binding adCreationFailedBinding() {
        return BindingBuilder.bind(adCreationFailedQueue()).to(choreographyExchange()).with(AD_CREATION_FAILED_KEY);
    }
}
