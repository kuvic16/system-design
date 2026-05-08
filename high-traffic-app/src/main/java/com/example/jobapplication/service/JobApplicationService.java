package com.example.jobapplication.service;

import com.example.jobapplication.entity.JobApplication;
import com.example.jobapplication.messaging.JobApplicationEventProducer;
import com.example.jobapplication.repository.JobApplicationRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer for JobApplication
 */
@Service
public class JobApplicationService {

    private static final int MAX_PAGE_SIZE = 50;

    private final JobApplicationRepository repository;
    private final JobApplicationEventProducer eventProducer;

    public JobApplicationService(JobApplicationRepository repository, JobApplicationEventProducer eventProducer) {
        this.repository = repository;
        this.eventProducer = eventProducer;
    }

    /**
     * Create a new job application
     */
    public JobApplication createJobApplication(JobApplication jobApplication) {
        eventProducer.publishCreatedEvent(jobApplication);
        return jobApplication;
    }

    /**
     * Get all job applications
     */
    @Cacheable(value = "applications", key = "#page + '-' + #size")
    public Page<JobApplication> getAllJobApplications(int page, int size) {
        System.out.println("Fetching from DB...");
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize);
        return repository.findAll(pageable);
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
