package com.SarankarDeveloperWebsite.controller;

import com.SarankarDeveloperWebsite.model.JobApplication;
import com.SarankarDeveloperWebsite.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/job-applications")
@CrossOrigin(origins = "*") // Configure this according to your frontend URL
public class JobApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(JobApplicationController.class);

    @Autowired
    private JobApplicationService jobApplicationService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            logger.error("Validation error for field {}: {}", fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<?> submitApplication(
            @Valid @RequestBody JobApplication application) {
        try {
            logger.info("Received job application submission - Name: {}, Email: {}", 
                application.getFullName(), application.getEmail());
            
            // Validate required fields
            if (application.getFullName() == null || application.getFullName().trim().isEmpty()) {
                logger.error("Full name is required");
                return ResponseEntity.badRequest().body("Full name is required");
            }
            if (application.getEmail() == null || application.getEmail().trim().isEmpty()) {
                logger.error("Email is required");
                return ResponseEntity.badRequest().body("Email is required");
            }
            if (application.getPhoneNumber() == null || application.getPhoneNumber().trim().isEmpty()) {
                logger.error("Phone number is required");
                return ResponseEntity.badRequest().body("Phone number is required");
            }
            if (application.getCurrentLocation() == null || application.getCurrentLocation().trim().isEmpty()) {
                logger.error("Current location is required");
                return ResponseEntity.badRequest().body("Current location is required");
            }
            if (application.getAboutYourself() == null || application.getAboutYourself().trim().isEmpty()) {
                logger.error("About yourself is required");
                return ResponseEntity.badRequest().body("About yourself is required");
            }

            JobApplication savedApplication = jobApplicationService.submitApplication(application, null);
            logger.info("Application submitted successfully - ID: {}", savedApplication.getId());
            return ResponseEntity.ok(savedApplication);
        } catch (Exception e) {
            logger.error("Error submitting application: " + e.getMessage(), e);
            String errorMessage = e.getMessage();
            if (errorMessage.contains("duplicate key error")) {
                errorMessage = "An application with this email already exists";
            }
            Map<String, String> error = new HashMap<>();
            error.put("error", errorMessage);
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping(value = "/resume", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadResume(
            @RequestParam("resume") MultipartFile resume,
            @RequestParam("applicationId") String applicationId) {
        try {
            logger.info("=== Starting Resume Upload Process ===");
            logger.info("Request Parameters - Application ID: {}", applicationId);
            logger.info("Resume File Details:");
            logger.info("- Original Filename: {}", resume.getOriginalFilename());
            logger.info("- Content Type: {}", resume.getContentType());
            logger.info("- Size: {} bytes", resume.getSize());
            logger.info("- Is Empty: {}", resume.isEmpty());
            
            // Validate application ID
            if (applicationId == null || applicationId.trim().isEmpty()) {
                logger.error("Application ID validation failed - ID is null or empty");
                return ResponseEntity.badRequest().body("Application ID is required");
            }

            // Validate resume file
            if (resume == null || resume.isEmpty()) {
                logger.error("Resume file validation failed - File is null or empty");
                return ResponseEntity.badRequest().body("No resume file provided");
            }

            // Validate PDF file type
            String contentType = resume.getContentType();
            String originalFilename = resume.getOriginalFilename();
            logger.info("Validating PDF file - Content Type: {}, Filename: {}", contentType, originalFilename);
            
            if (contentType == null || !contentType.equals("application/pdf")) {
                logger.error("PDF validation failed - Invalid content type: {}", contentType);
                return ResponseEntity.badRequest().body("Only PDF files are allowed. Please upload your resume in PDF format.");
            }

            // Validate file extension
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
                logger.error("PDF validation failed - Invalid file extension: {}", originalFilename);
                return ResponseEntity.badRequest().body("Invalid file format. Please upload a PDF file.");
            }

            // Validate file size (5MB max for PDF)
            long maxSize = 5 * 1024 * 1024; // 5MB in bytes
            if (resume.getSize() > maxSize) {
                logger.error("File size validation failed - Size: {} bytes, Max allowed: {} bytes", resume.getSize(), maxSize);
                return ResponseEntity.badRequest().body("PDF file size exceeds the maximum limit of 5MB");
            }

            logger.info("All validations passed, proceeding with resume upload");
            
            // Process the resume upload
            JobApplication updatedApplication = jobApplicationService.uploadResume(applicationId, resume);
            
            logger.info("=== Resume Upload Process Completed ===");
            logger.info("Application Details After Upload:");
            logger.info("- ID: {}", updatedApplication.getId());
            logger.info("- Resume URL: {}", updatedApplication.getResumeUrl());
            logger.info("- Full Name: {}", updatedApplication.getFullName());
            logger.info("- Email: {}", updatedApplication.getEmail());
            
            if (updatedApplication.getResumeUrl() == null) {
                logger.error("Resume URL is null after successful upload!");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: Resume was uploaded but URL was not saved. Please contact support.");
            }
            
            return ResponseEntity.ok(updatedApplication);
        } catch (Exception e) {
            logger.error("=== Resume Upload Process Failed ===");
            logger.error("Error details:", e);
            String errorMessage = e.getMessage();
            
            // Map specific exceptions to user-friendly messages
            if (errorMessage.contains("not found")) {
                errorMessage = "Application not found";
            } else if (errorMessage.contains("file size")) {
                errorMessage = "PDF file size exceeds the maximum limit of 5MB";
            } else if (errorMessage.contains("file type") || errorMessage.contains("format")) {
                errorMessage = "Only PDF files are allowed. Please upload your resume in PDF format.";
            }
            
            return ResponseEntity.badRequest().body(errorMessage);
        }
    }

    @GetMapping
    public ResponseEntity<List<JobApplication>> getAllApplications() {
        try {
            List<JobApplication> applications = jobApplicationService.getAllApplications();
            logger.info("Retrieved {} applications", applications.size());
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            logger.error("Error retrieving applications: " + e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getApplicationById(@PathVariable String id) {
        try {
            JobApplication application = jobApplicationService.getApplicationById(id);
            logger.info("Retrieved application with ID: {}", id);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            logger.error("Error retrieving application {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
} 