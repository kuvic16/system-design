package com.example.jobapplication.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes dead-lettered job application events.
 * Logs and handles messages that failed processing in the main consumer.
 */
@Component
public class JobApplicationDeadLetterConsumer {

    private static final Logger logger = LoggerFactory.getLogger(JobApplicationDeadLetterConsumer.class);

    /**
     * Listen on the Dead Letter Queue.
     * Messages arrive here when they are rejected without requeue from the main queue.
     *
     * @param event the dead-lettered event
     */
    @RabbitListener(queues = "${app.rabbitmq.dlq}", concurrency = "5")
    public void handleDeadLetterEvent(JobApplicationCreatedEvent event) {
        logger.error("DEAD LETTERED MESSAGE - Job application event failed permanently and was moved to DLQ. "
                + "name={}, email={}, jobId={}, resumeUrl={}", 
                event.getName(), 
                event.getEmail(), 
                event.getJobId(), 
                event.getResumeUrl());
        
        // In production, you might want to:
        // 1. Store the message in a database for audit trail
        // 2. Send a notification/alert to ops
        // 3. Expose an admin endpoint to retry/replay dead-lettered messages
    }
}

