package com.example.jobapplication.controller;

import com.example.jobapplication.entity.JobApplication;
import com.example.jobapplication.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Job Application API
 */
@RestController
@RequestMapping("/api/job-applications")
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
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET - Retrieve all job applications
     */
    @GetMapping
    public ResponseEntity<List<JobApplication>> getAllJobApplications() {
        List<JobApplication> applications = service.getAllJobApplications();
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

