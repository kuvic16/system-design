package com.example.jobapplication.messaging;

import com.example.jobapplication.entity.JobApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Publishes job application events to RabbitMQ.
 */
@Component
public class JobApplicationEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(JobApplicationEventProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String routingKey;

    public JobApplicationEventProducer(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.exchange}") String exchangeName,
            @Value("${app.rabbitmq.routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    public void publishCreatedEvent(JobApplication jobApplication) {
        JobApplicationCreatedEvent event = new JobApplicationCreatedEvent(
                jobApplication.getName(),
                jobApplication.getEmail(),
                jobApplication.getJobId(),
                jobApplication.getResumeUrl()
        );
        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
        logger.debug("Published job application created event. name={}, email={}, jobId={}, exchange={}, routingKey={}",
                event.getName(), event.getEmail(), event.getJobId(), exchangeName, routingKey);
    }
}

