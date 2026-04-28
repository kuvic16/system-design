package com.example.jobapplication.service;

import com.example.jobapplication.entity.JobApplication;
import com.example.jobapplication.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for JobApplication
 */
@Service
public class JobApplicationService {

    private final JobApplicationRepository repository;

    public JobApplicationService(JobApplicationRepository repository) {
        this.repository = repository;
    }

    /**
     * Create a new job application
     */
    public JobApplication createJobApplication(JobApplication jobApplication) {
        return repository.save(jobApplication);
    }

    /**
     * Get all job applications
     */
    public List<JobApplication> getAllJobApplications() {
        return repository.findAll();
    }

    /**
     * Get a job application by ID
     */
    public Optional<JobApplication> getJobApplicationById(Long id) {
        return repository.findById(id);
    }

    /**
     * Update an existing job application
     */
    public JobApplication updateJobApplication(Long id, JobApplication jobApplication) {
        return repository.findById(id).map(existing -> {
            existing.setName(jobApplication.getName());
            existing.setEmail(jobApplication.getEmail());
            existing.setJobId(jobApplication.getJobId());
            existing.setResumeUrl(jobApplication.getResumeUrl());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Job Application not found with id: " + id));
    }

    /**
     * Delete a job application
     */
    public void deleteJobApplication(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Job Application not found with id: " + id);
        }
        repository.deleteById(id);
    }
}

