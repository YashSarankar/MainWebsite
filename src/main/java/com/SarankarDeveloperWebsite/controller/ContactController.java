package com.SarankarDeveloperWebsite.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @PostMapping
    public ResponseEntity<?> submitContactForm(@RequestBody Map<String, String> formData) {
        try {
            logger.info("Received contact form submission - Name: {}, Email: {}", 
                formData.get("name"), formData.get("email"));
            
            // Validate required fields
            if (formData.get("name") == null || formData.get("name").trim().isEmpty()) {
                logger.error("Name is required");
                return ResponseEntity.badRequest().body("Name is required");
            }
            if (formData.get("email") == null || formData.get("email").trim().isEmpty()) {
                logger.error("Email is required");
                return ResponseEntity.badRequest().body("Email is required");
            }
            if (formData.get("subject") == null || formData.get("subject").trim().isEmpty()) {
                logger.error("Subject is required");
                return ResponseEntity.badRequest().body("Subject is required");
            }
            if (formData.get("message") == null || formData.get("message").trim().isEmpty()) {
                logger.error("Message is required");
                return ResponseEntity.badRequest().body("Message is required");
            }

            // TODO: Implement email sending or other notification logic here
            
            logger.info("Contact form submitted successfully");
            Map<String, String> response = new HashMap<>();
            response.put("message", "Thank you for your message! We will get back to you soon.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing contact form: " + e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Sorry, there was an error processing your message. Please try again later.");
            return ResponseEntity.internalServerError().body(error);
        }
    }
} 