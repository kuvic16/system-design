package com.example.jobapplication.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ queue topology configuration.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.queue}")
    private String queueName;

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Bean
    public Queue jobApplicationQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public TopicExchange jobApplicationExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding jobApplicationBinding(Queue jobApplicationQueue, TopicExchange jobApplicationExchange) {
        return BindingBuilder.bind(jobApplicationQueue).to(jobApplicationExchange).with(routingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

