package com.SarankarDeveloperWebsite.repository;

import com.SarankarDeveloperWebsite.model.JobApplication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobApplicationRepository extends MongoRepository<JobApplication, String> {
    // Custom query methods can be added here if needed
    boolean existsByEmail(String email);
} 