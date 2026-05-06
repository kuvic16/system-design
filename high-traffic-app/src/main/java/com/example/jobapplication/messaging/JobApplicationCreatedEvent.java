package com.example.jobapplication.messaging;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Event DTO for job application creation.
 * Sent over RabbitMQ without the database-generated ID.
 */
public class JobApplicationCreatedEvent {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Job ID is required")
    private Long jobId;

    @NotBlank(message = "Resume URL is required")
    private String resumeUrl;

    // No-arg constructor for Jackson deserialization
    public JobApplicationCreatedEvent() {}

    public JobApplicationCreatedEvent(String name, String email, Long jobId, String resumeUrl) {
        this.name = name;
        this.email = email;
        this.jobId = jobId;
        this.resumeUrl = resumeUrl;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }
}

