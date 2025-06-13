package com.SarankarDeveloperWebsite.service;

import com.SarankarDeveloperWebsite.model.JobApplication;
import com.SarankarDeveloperWebsite.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import java.nio.file.StandardCopyOption;

@Service
public class JobApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(JobApplicationService.class);

    @Autowired
    private JobApplicationRepository jobApplicationRepository;
    
    private final Path fileStorageLocation = Paths.get("uploads/resumes").toAbsolutePath().normalize();

    public JobApplicationService() {
        try {
            Files.createDirectories(fileStorageLocation);
            logger.info("Resume upload directory created at: {}", fileStorageLocation);
        } catch (IOException ex) {
            logger.error("Failed to create resume upload directory: {}", ex.getMessage());
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public JobApplication submitApplication(JobApplication application, MultipartFile resume) {
        logger.info("Processing application submission for email: {}", application.getEmail());
        
        try {
            if (jobApplicationRepository.existsByEmail(application.getEmail())) {
                logger.warn("Duplicate application attempt for email: {}", application.getEmail());
                throw new RuntimeException("An application with this email already exists");
            }

            if (resume != null && !resume.isEmpty()) {
                logger.info("Processing resume upload: {}", resume.getOriginalFilename());
                String resumeUrl = storeResume(resume);
                application.setResumeUrl(resumeUrl);
                logger.info("Resume stored successfully: {}", resumeUrl);
            }

            JobApplication savedApplication = jobApplicationRepository.save(application);
            logger.info("Application saved successfully with ID: {}", savedApplication.getId());
            return savedApplication;
        } catch (Exception e) {
            logger.error("Error processing application: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process application: " + e.getMessage(), e);
        }
    }

    @Transactional
    public JobApplication uploadResume(String applicationId, MultipartFile resume) {
        logger.info("=== Starting Resume Upload Service ===");
        logger.info("Processing resume upload for application ID: {}", applicationId);
        
        // Find the application
        JobApplication application = jobApplicationRepository.findById(applicationId)
            .orElseThrow(() -> {
                logger.error("Application not found with ID: {}", applicationId);
                return new RuntimeException("Application not found with ID: " + applicationId);
            });
        
        logger.info("Found application - ID: {}, Email: {}", application.getId(), application.getEmail());
        
        try {
            // Validate file
            if (resume == null || resume.isEmpty()) {
                logger.error("Resume file validation failed in service - File is null or empty");
                throw new RuntimeException("No resume file provided");
            }

            // Validate PDF file
            String originalFilename = resume.getOriginalFilename();
            logger.info("Validating PDF file in service - Original filename: {}", originalFilename);
            
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
                logger.error("PDF validation failed in service - Invalid filename: {}", originalFilename);
                throw new RuntimeException("Only PDF files are allowed");
            }

            String contentType = resume.getContentType();
            logger.info("Validating PDF content type in service - Content type: {}", contentType);
            
            if (contentType == null || !contentType.equals("application/pdf")) {
                logger.error("PDF validation failed in service - Invalid content type: {}", contentType);
                throw new RuntimeException("Invalid file type. Only PDF files are allowed.");
            }

            // Generate unique filename with .pdf extension
            String uniqueFilename = applicationId + "_" + System.currentTimeMillis() + ".pdf";
            logger.info("Generated unique filename: {}", uniqueFilename);
            
            // Create uploads directory if it doesn't exist
            Path uploadDir = Paths.get("uploads/resumes");
            if (!Files.exists(uploadDir)) {
                logger.info("Creating upload directory: {}", uploadDir);
                Files.createDirectories(uploadDir);
            }
            
            // Save the file
            Path filePath = uploadDir.resolve(uniqueFilename);
            logger.info("Saving PDF resume to: {}", filePath);
            
            // Copy the file to the uploads directory
            Files.copy(resume.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("PDF file saved successfully at: {}", filePath);
            
            // Update application with resume URL
            String resumeUrl = "/uploads/resumes/" + uniqueFilename;
            logger.info("Setting resume URL: {}", resumeUrl);
            application.setResumeUrl(resumeUrl);
            
            // Save the updated application
            logger.info("Saving updated application with resume URL");
            JobApplication savedApplication = jobApplicationRepository.save(application);
            
            // Verify the save
            if (savedApplication.getResumeUrl() == null) {
                logger.error("Resume URL is null after saving application!");
                throw new RuntimeException("Failed to save resume URL to application");
            }
            
            logger.info("=== Resume Upload Service Completed Successfully ===");
            logger.info("Final application state:");
            logger.info("- ID: {}", savedApplication.getId());
            logger.info("- Resume URL: {}", savedApplication.getResumeUrl());
            logger.info("- Email: {}", savedApplication.getEmail());
            
            return savedApplication;
        } catch (IOException e) {
            logger.error("=== Resume Upload Service Failed - IO Error ===");
            logger.error("Error saving PDF resume file: {}", e.getMessage(), e);
            throw new RuntimeException("Error saving PDF resume file: " + e.getMessage());
        } catch (Exception e) {
            logger.error("=== Resume Upload Service Failed ===");
            logger.error("Error processing PDF resume upload: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing PDF resume upload: " + e.getMessage());
        }
    }

    private String storeResume(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path targetLocation = fileStorageLocation.resolve(fileName);
            logger.info("Storing resume at: {}", targetLocation);
            
            Files.copy(file.getInputStream(), targetLocation);
            logger.info("Resume stored successfully");
            
            return fileName;
        } catch (IOException ex) {
            logger.error("Failed to store resume: {}", ex.getMessage(), ex);
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    public List<JobApplication> getAllApplications() {
        logger.info("Retrieving all applications");
        try {
            List<JobApplication> applications = jobApplicationRepository.findAll();
            logger.info("Retrieved {} applications", applications.size());
            return applications;
        } catch (Exception e) {
            logger.error("Error retrieving applications: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve applications", e);
        }
    }

    public JobApplication getApplicationById(String id) {
        logger.info("Retrieving application with ID: {}", id);
        try {
            return jobApplicationRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Application not found with ID: {}", id);
                        return new RuntimeException("Application not found with id: " + id);
                    });
        } catch (Exception e) {
            logger.error("Error retrieving application {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve application", e);
        }
    }
} 