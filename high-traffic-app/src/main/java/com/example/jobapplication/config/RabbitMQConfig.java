package com.example.jobapplication.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ queue topology configuration with Dead Letter Queue support.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.queue}")
    private String queueName;

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${app.rabbitmq.dlx}")
    private String dlxName;

    @Value("${app.rabbitmq.dlq}")
    private String dlqName;

    @Value("${app.rabbitmq.dlq-routing-key}")
    private String dlqRoutingKey;

    @Value("${app.rabbitmq.retry-queue}")
    private String retryQueueName;

    @Value("${app.rabbitmq.retry-routing-key}")
    private String retryRoutingKey;

    @Value("${app.rabbitmq.retry-delay-ms:10000}")
    private long retryDelayMs;

    /**
     * Main queue with dead-letter exchange configuration.
     * Messages rejected without requeue go to the DLX.
     */
    @Bean
    public Queue jobApplicationQueue() {
        return new Queue(queueName, true, false, false,
                java.util.Map.of(
                    "x-dead-letter-exchange", dlxName,
                    "x-dead-letter-routing-key", dlqRoutingKey
                )
        );
    }

    @Bean
    public TopicExchange jobApplicationExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding jobApplicationBinding(Queue jobApplicationQueue, TopicExchange jobApplicationExchange) {
        return BindingBuilder.bind(jobApplicationQueue).to(jobApplicationExchange).with(routingKey);
    }

    /**
     * Retry queue for delayed reprocessing; messages published with retry routing key
     * wait for retryDelayMs, then return to the main queue.
     */
    @Bean
    public Queue jobApplicationRetryQueue() {
        return new Queue(retryQueueName, true, false, false,
                java.util.Map.of(
                    "x-message-ttl", retryDelayMs,
                    "x-dead-letter-exchange", exchangeName,
                    "x-dead-letter-routing-key", routingKey
                )
        );
    }

    @Bean
    public Binding jobApplicationRetryBinding(Queue jobApplicationRetryQueue, TopicExchange jobApplicationExchange) {
        return BindingBuilder.bind(jobApplicationRetryQueue).to(jobApplicationExchange).with(retryRoutingKey);
    }

    /**
     * Dead Letter Exchange for failed messages.
     */
    @Bean
    public TopicExchange jobApplicationDlx() {
        return new TopicExchange(dlxName);
    }

    /**
     * Dead Letter Queue for messages that failed processing.
     */
    @Bean
    public Queue jobApplicationDlq() {
        return new Queue(dlqName, true);
    }

    /**
     * Binding from DLX to DLQ.
     */
    @Bean
    public Binding jobApplicationDlqBinding(Queue jobApplicationDlq, TopicExchange jobApplicationDlx) {
        return BindingBuilder.bind(jobApplicationDlq).to(jobApplicationDlx).with(dlqRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Listener container factory configured to NOT requeue rejected messages.
     * When a message fails (exception thrown by consumer), it is rejected without requeue,
     * which triggers RabbitMQ to route it to the Dead Letter Queue (DLQ) instead of
     * looping forever back into the main queue.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        // KEY: reject without requeue so failed messages go to DLQ, not loop forever
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}

