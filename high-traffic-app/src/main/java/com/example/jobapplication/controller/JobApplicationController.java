package com.example.jobapplication.controller;

import com.example.jobapplication.entity.JobApplication;
import com.example.jobapplication.ratelimit.RateLimited;
import com.example.jobapplication.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Job Application API
 */
@RestController
@RequestMapping("/api/job-applications")
@RateLimited(bucket = "job-applications:other", limitProperty = "app.rate-limit.other-requests")
public class JobApplicationController {

    private final JobApplicationService service;

    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    /**
     * POST - Create a new job application
     */
    @PostMapping
    public ResponseEntity<JobApplication> createJobApplication(@Valid @RequestBody JobApplication jobApplication) {
        JobApplication created = service.createJobApplication(jobApplication);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(created);
    }

    /**
     * GET - Retrieve job applications with pagination
     */
    @GetMapping
    @RateLimited(bucket = "job-applications:list", limitProperty = "app.rate-limit.get-all-requests")
    public ResponseEntity<Page<JobApplication>> getAllJobApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<JobApplication> applications = service.getAllJobApplications(page, size);
        return ResponseEntity.ok(applications);
    }

    /**
     * GET - Retrieve a specific job application by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobApplication> getJobApplicationById(@PathVariable Long id) {
        return service.getJobApplicationById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * PUT - Update an existing job application
     */
    @PutMapping("/{id}")
    public ResponseEntity<JobApplication> updateJobApplication(
            @PathVariable Long id,
            @Valid @RequestBody JobApplication jobApplication) {
        try {
            JobApplication updated = service.updateJobApplication(id, jobApplication);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE - Delete a job application
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobApplication(@PathVariable Long id) {
        try {
            service.deleteJobApplication(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
