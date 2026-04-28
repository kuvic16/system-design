package com.example.jobapplication.repository;

import com.example.jobapplication.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for JobApplication entity
 */
@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
}

