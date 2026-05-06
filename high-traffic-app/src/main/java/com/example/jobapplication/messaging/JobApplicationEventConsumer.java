package com.example.jobapplication.messaging;

import com.example.jobapplication.entity.JobApplication;
import com.example.jobapplication.repository.JobApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes job application events from RabbitMQ.
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
     }
}

