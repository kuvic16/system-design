package com.example.jobapplication.messaging;

import com.example.jobapplication.entity.JobApplication;
import com.example.jobapplication.repository.JobApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes job application events from RabbitMQ.
 * On unrecoverable failures, throws an exception to trigger dead-lettering.
 */
@Component
public class JobApplicationEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(JobApplicationEventConsumer.class);

    private final JobApplicationRepository repository;

    public JobApplicationEventConsumer(JobApplicationRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void consumeCreatedEvent(JobApplicationCreatedEvent event) {
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
        } catch (IllegalArgumentException e) {
            // Unrecoverable validation error: throw to trigger dead-lettering
            logger.error("Unrecoverable validation error. Rejecting message to send to DLQ. error={}", e.getMessage());
            throw e;
        } catch (Exception e) {
            // Other errors (e.g., database constraint, transient issues)
            // Throw exception to let listener container handle it
            // With default config, this will trigger dead-lettering after max retries
            logger.error("Error processing job application event. error={}", e.getMessage(), e);
            // Wrap in RuntimeException to ensure it's treated as unrecoverable
            throw new RuntimeException("Failed to process job application event: " + e.getMessage(), e);
        }
    }
}

