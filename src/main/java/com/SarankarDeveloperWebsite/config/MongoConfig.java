package com.SarankarDeveloperWebsite.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bson.Document;

@Configuration
public class MongoConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            try {
                // Test the connection by executing a simple command
                mongoTemplate.getDb().runCommand(new Document("ping", 1));
                logger.info("Successfully connected to MongoDB!");
            } catch (Exception e) {
                logger.error("Failed to connect to MongoDB: " + e.getMessage());
                throw e;
            }
        };
    }
} 