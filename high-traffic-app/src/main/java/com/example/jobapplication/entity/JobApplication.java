package com.example.jobapplication.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * Entity class representing a job application
 */
@Entity
@Table(name = "job_applications")
public class JobApplication implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false)
    private String email;

    @NotNull(message = "Job ID is required")
    @Column(nullable = false)
    private Long jobId;

    @NotBlank(message = "Resume URL is required")
    @Column(nullable = false)
    private String resumeUrl;

    // No-arg constructor (required by JPA)
    public JobApplication() {}

    // All-args constructor
    public JobApplication(Long id, String name, String email, Long jobId, String resumeUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.jobId = jobId;
        this.resumeUrl = resumeUrl;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Long getJobId() { return jobId; }
    public String getResumeUrl() { return resumeUrl; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
}
