package com.SarankarDeveloperWebsite.config;

import com.SarankarDeveloperWebsite.model.PortfolioItem;
import com.SarankarDeveloperWebsite.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(PortfolioRepository portfolioRepository) {
        return args -> {
            // Only initialize if the collection is empty
            if (portfolioRepository.count() == 0) {
                logger.info("Initializing portfolio data...");
                
                List<PortfolioItem> portfolioItems = Arrays.asList(
                    createPortfolioItem(
                        "E-commerce Platform",
                        "A modern e-commerce solution with advanced features like real-time inventory, secure payments, and personalized shopping experience.",
                        "web",
                        "https://images.unsplash.com/photo-1547658719-da2b51169166?w=800&auto=format&fit=crop&q=60",
                        Arrays.asList("React", "Node.js", "MongoDB", "Stripe"),
                        "#",
                        "https://github.com/pratham-sarankar"
                    ),
                    createPortfolioItem(
                        "Fitness Tracking App",
                        "A comprehensive fitness application with workout tracking, nutrition planning, and social features for fitness enthusiasts.",
                        "mobile",
                        "https://images.unsplash.com/photo-1551650975-87deedd944c3?w=800&auto=format&fit=crop&q=60",
                        Arrays.asList("Flutter", "Firebase", "REST API", "Google Fit"),
                        "#",
                        "https://github.com/pratham-sarankar"
                    ),
                    createPortfolioItem(
                        "Inventory Management System",
                        "Enterprise-grade inventory management solution with real-time tracking, automated ordering, and comprehensive reporting.",
                        "software",
                        "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&auto=format&fit=crop&q=60",
                        Arrays.asList("Python", "Django", "PostgreSQL", "Docker"),
                        "#",
                        "https://github.com/pratham-sarankar"
                    ),
                    createPortfolioItem(
                        "Business Analytics Dashboard",
                        "Interactive dashboard for business analytics with real-time data visualization, custom reporting, and KPI tracking.",
                        "web",
                        "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800&auto=format&fit=crop&q=60",
                        Arrays.asList("Vue.js", "D3.js", "Node.js", "MongoDB"),
                        "#",
                        "https://github.com/pratham-sarankar"
                    ),
                    createPortfolioItem(
                        "Food Delivery App",
                        "On-demand food delivery application with real-time tracking, restaurant management, and seamless payment integration.",
                        "mobile",
                        "https://images.unsplash.com/photo-1555774698-0b77e0d5fac6?w=800&auto=format&fit=crop&q=60",
                        Arrays.asList("React Native", "Redux", "Firebase", "Google Maps"),
                        "#",
                        "https://github.com/pratham-sarankar"
                    ),
                    createPortfolioItem(
                        "Project Management Tool",
                        "Comprehensive project management solution with task tracking, team collaboration, and resource management features.",
                        "software",
                        "https://images.unsplash.com/photo-1551434678-e076c223a692?w=800&auto=format&fit=crop&q=60",
                        Arrays.asList("Angular", "Spring Boot", "MySQL", "AWS"),
                        "#",
                        "https://github.com/pratham-sarankar"
                    )
                );

                portfolioRepository.saveAll(portfolioItems);
                logger.info("Portfolio data initialized successfully!");
            } else {
                logger.info("Portfolio data already exists, skipping initialization.");
            }
        };
    }

    private PortfolioItem createPortfolioItem(
            String title,
            String description,
            String category,
            String image,
            List<String> technologies,
            String liveUrl,
            String githubUrl) {
        
        PortfolioItem item = new PortfolioItem();
        item.setTitle(title);
        item.setDescription(description);
        item.setCategory(category);
        item.setImage(image);
        item.setTechnologies(technologies);
        item.setLiveUrl(liveUrl);
        item.setGithubUrl(githubUrl);
        return item;
    }
} 