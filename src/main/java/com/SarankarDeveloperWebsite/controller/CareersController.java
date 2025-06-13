package com.SarankarDeveloperWebsite.controller;

import com.SarankarDeveloperWebsite.model.Career;
import com.SarankarDeveloperWebsite.service.CareerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/careers")
@CrossOrigin(origins = {"http://127.0.0.1:5502", "http://localhost:5502"}, allowCredentials = "true")
public class CareersController {
    private static final Logger logger = LoggerFactory.getLogger(CareersController.class);

    @Autowired
    private CareerService careerService;

    @GetMapping
    public ResponseEntity<List<Career>> getAllCareers() {
        logger.info("Received request to get all career listings");
        List<Career> careers = careerService.getAllCareers();
        logger.info("Returning {} career listings", careers.size());
        return ResponseEntity.ok(careers);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Career>> getCareersByCategory(@PathVariable String category) {
        logger.info("Received request to get career listings for category: {}", category);
        List<Career> careers = careerService.getCareersByCategory(category);
        logger.info("Returning {} career listings for category {}", careers.size(), category);
        return ResponseEntity.ok(careers);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Career>> getCareersByType(@PathVariable String type) {
        logger.info("Received request to get career listings for type: {}", type);
        List<Career> careers = careerService.getCareersByType(type);
        logger.info("Returning {} career listings for type {}", careers.size(), type);
        return ResponseEntity.ok(careers);
    }

    @PostMapping("/apply")
    public ResponseEntity<?> submitApplication(@RequestBody Map<String, Object> application) {
        try {
            logger.info("Received job application for position: {}", application.get("jobTitle"));
            // TODO: Implement application processing logic
            Map<String, String> response = new HashMap<>();
            response.put("message", "Application received successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing job application: " + e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unable to process application. Please try again later.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping
    public ResponseEntity<Career> createCareer(@RequestBody Career career) {
        logger.info("Received request to create new career listing: {}", career.getTitle());
        Career savedCareer = careerService.saveCareer(career);
        logger.info("Created new career listing with id: {}", savedCareer.getId());
        return ResponseEntity.ok(savedCareer);
    }
} 
