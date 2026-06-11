package com.example.jobapplication.messaging;

import com.example.jobapplication.entity.JobApplication;
import com.example.jobapplication.repository.JobApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Consumes job application events from RabbitMQ.
 * On failures, sends messages to retry queue first and then to DLQ after max attempts.
 */
@Component
public class JobApplicationEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(JobApplicationEventConsumer.class);

    private final JobApplicationRepository repository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.retry-routing-key}")
    private String retryRoutingKey;

    @Value("${app.rabbitmq.dlx}")
    private String dlxName;

    @Value("${app.rabbitmq.dlq-routing-key}")
    private String dlqRoutingKey;

    @Value("${app.rabbitmq.max-retries:3}")
    private int maxRetries;

    private static final String RETRY_HEADER = "x-retry-count";

    public JobApplicationEventConsumer(JobApplicationRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}", concurrency = "5")
    public void consumeCreatedEvent(JobApplicationCreatedEvent event, Message message) {
        int retryCount = getRetryCount(message);
        try {
            // Validate event data
            if (event == null || event.getName() == null || event.getEmail() == null) {
                logger.warn("Received invalid event with missing required fields. event={}", event);
                throw new IllegalArgumentException("Invalid event: missing required fields (name or email)");
            }

            // Build and persist the job application
            JobApplication jobApplication = new JobApplication();
            jobApplication.setName(event.getName());
            jobApplication.setEmail(event.getEmail());
            jobApplication.setJobId(event.getJobId());
            jobApplication.setResumeUrl(event.getResumeUrl());

            JobApplication saved = repository.save(jobApplication);
            logger.info("Received and persisted job application event. id={}, name={}, email={}, jobId={}",
                    saved.getId(),
                    saved.getName(),
                    saved.getEmail(),
                    saved.getJobId());
        } catch (Exception e) {
            handleFailure(event, retryCount, e);
        }
    }

    private void handleFailure(JobApplicationCreatedEvent event, int retryCount, Exception ex) {
        if (retryCount < maxRetries) {
            int nextRetry = retryCount + 1;
            rabbitTemplate.convertAndSend(exchangeName, retryRoutingKey, event, withRetryHeader(nextRetry));
            logger.warn("Processing failed. Sent event to retry queue. retryAttempt={}/{}, error={}",
                    nextRetry, maxRetries, ex.getMessage());
            return;
        }

        rabbitTemplate.convertAndSend(dlxName, dlqRoutingKey, event, withRetryHeader(retryCount));
        logger.error("Processing failed after max retries. Sent event to DLQ. retryAttempt={}, error={}",
                retryCount, ex.getMessage(), ex);
    }

    private int getRetryCount(Message message) {
        Object value = message.getMessageProperties().getHeaders().get(RETRY_HEADER);
        if (value instanceof Number number) {
            return number.intValue();
        }
        return 0;
    }

    private MessagePostProcessor withRetryHeader(int retryCount) {
        return msg -> {
            Map<String, Object> headers = new HashMap<>(msg.getMessageProperties().getHeaders());
            headers.put(RETRY_HEADER, retryCount);
            msg.getMessageProperties().setHeaders(headers);
            return msg;
        };
    }
}

