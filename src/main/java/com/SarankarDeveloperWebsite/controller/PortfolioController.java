package com.SarankarDeveloperWebsite.controller;

import com.SarankarDeveloperWebsite.model.PortfolioItem;
import com.SarankarDeveloperWebsite.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "*") // Configure this according to your frontend URL
public class PortfolioController {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioController.class);

    @Autowired
    private PortfolioService portfolioService;

    @GetMapping
    public ResponseEntity<List<PortfolioItem>> getAllPortfolioItems() {
        try {
            logger.info("Received request for all portfolio items");
            List<PortfolioItem> items = portfolioService.getAllPortfolioItems();
            logger.info("Retrieved {} portfolio items", items.size());
            if (items.isEmpty()) {
                logger.warn("No portfolio items found in the database");
            } else {
                items.forEach(item -> logger.debug("Portfolio item: id={}, title={}, category={}", 
                    item.getId(), item.getTitle(), item.getCategory()));
            }
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            logger.error("Error retrieving portfolio items: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<PortfolioItem>> getPortfolioItemsByCategory(@PathVariable String category) {
        try {
            logger.info("Received request for portfolio items in category: {}", category);
            List<PortfolioItem> items = portfolioService.getPortfolioItemsByCategory(category);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            logger.error("Error retrieving portfolio items for category {}: {}", category, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<PortfolioItem> createPortfolioItem(@RequestBody PortfolioItem item) {
        try {
            logger.info("Received request to create portfolio item: {}", item.getTitle());
            PortfolioItem savedItem = portfolioService.savePortfolioItem(item);
            return ResponseEntity.ok(savedItem);
        } catch (Exception e) {
            logger.error("Error creating portfolio item: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 