package com.SarankarDeveloperWebsite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.SarankarDeveloperWebsite.model.JobApplication;
import com.SarankarDeveloperWebsite.repository.JobApplicationRepository;
import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @GetMapping("/mongodb-connection")
    public String testMongoDBConnection() {
        try {
            // Try to execute a simple command
            mongoTemplate.getDb().runCommand(new Document("ping", 1));
            logger.info("MongoDB connection test successful!");
            return "MongoDB is connected successfully! Database name: " + mongoTemplate.getDb().getName();
        } catch (Exception e) {
            logger.error("MongoDB connection test failed: " + e.getMessage());
            return "MongoDB connection failed: " + e.getMessage();
        }
    }

    @GetMapping("/job-applications")
    public List<JobApplication> getJobApplications() {
        logger.info("Retrieving all job applications for debugging");
        List<JobApplication> applications = jobApplicationRepository.findAll();
        logger.info("Found {} applications", applications.size());
        
        // Log each application's details
        for (JobApplication app : applications) {
            logger.info("Application Details:");
            logger.info("- ID: {}", app.getId());
            logger.info("- Email: {}", app.getEmail());
            logger.info("- Full Name: {}", app.getFullName());
            logger.info("- Resume URL: {}", app.getResumeUrl());
            logger.info("- Phone: {}", app.getPhoneNumber());
            logger.info("- Location: {}", app.getCurrentLocation());
            logger.info("-------------------");
        }
        
        return applications;
    }

    @GetMapping("/job-applications/{id}")
    public JobApplication getJobApplication(String id) {
        logger.info("Retrieving job application with ID: {}", id);
        JobApplication application = jobApplicationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        
        logger.info("Application Details:");
        logger.info("- ID: {}", application.getId());
        logger.info("- Email: {}", application.getEmail());
        logger.info("- Full Name: {}", application.getFullName());
        logger.info("- Resume URL: {}", application.getResumeUrl());
        logger.info("- Phone: {}", application.getPhoneNumber());
        logger.info("- Location: {}", application.getCurrentLocation());
        
        return application;
    }
} 