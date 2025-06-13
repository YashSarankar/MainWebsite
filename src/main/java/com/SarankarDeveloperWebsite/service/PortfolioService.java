package com.SarankarDeveloperWebsite.service;

import com.SarankarDeveloperWebsite.model.PortfolioItem;
import com.SarankarDeveloperWebsite.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PortfolioService {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioService.class);

    @Autowired
    private PortfolioRepository portfolioRepository;

    public List<PortfolioItem> getAllPortfolioItems() {
        logger.info("Retrieving all portfolio items from database");
        try {
            List<PortfolioItem> items = portfolioRepository.findAll();
            logger.info("Successfully retrieved {} portfolio items from database", items.size());
            if (items.isEmpty()) {
                logger.warn("No portfolio items found in database. This might indicate an initialization issue.");
            } else {
                items.forEach(item -> logger.debug("Found portfolio item: id={}, title={}, category={}", 
                    item.getId(), item.getTitle(), item.getCategory()));
            }
            return items;
        } catch (Exception e) {
            logger.error("Error retrieving portfolio items from database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve portfolio items", e);
        }
    }

    public List<PortfolioItem> getPortfolioItemsByCategory(String category) {
        logger.info("Retrieving portfolio items for category: {}", category);
        try {
            List<PortfolioItem> items = portfolioRepository.findByCategory(category);
            logger.info("Retrieved {} portfolio items for category {}", items.size(), category);
            return items;
        } catch (Exception e) {
            logger.error("Error retrieving portfolio items for category {}: {}", category, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve portfolio items for category: " + category, e);
        }
    }

    public PortfolioItem savePortfolioItem(PortfolioItem item) {
        logger.info("Saving portfolio item: {}", item.getTitle());
        try {
            PortfolioItem savedItem = portfolioRepository.save(item);
            logger.info("Saved portfolio item with ID: {}", savedItem.getId());
            return savedItem;
        } catch (Exception e) {
            logger.error("Error saving portfolio item: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save portfolio item", e);
        }
    }
} 