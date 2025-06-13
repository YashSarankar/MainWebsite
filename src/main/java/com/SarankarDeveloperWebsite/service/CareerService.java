package com.SarankarDeveloperWebsite.service;

import com.SarankarDeveloperWebsite.model.Career;
import com.SarankarDeveloperWebsite.repository.CareerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Arrays;

@Service
public class CareerService {
    private static final Logger logger = LoggerFactory.getLogger(CareerService.class);
    
    @Autowired
    private CareerRepository careerRepository;

    public List<Career> getAllCareers() {
        logger.info("Fetching all career listings from database");
        List<Career> careers = careerRepository.findAll();
        logger.info("Found {} career listings", careers.size());
        return careers;
    }

    public List<Career> getCareersByCategory(String category) {
        logger.info("Fetching career listings for category: {}", category);
        List<Career> careers = careerRepository.findByCategory(category);
        logger.info("Found {} career listings for category {}", careers.size(), category);
        return careers;
    }

    public List<Career> getCareersByType(String type) {
        logger.info("Fetching career listings for type: {}", type);
        List<Career> careers = careerRepository.findByType(type);
        logger.info("Found {} career listings for type {}", careers.size(), type);
        return careers;
    }

    public Career saveCareer(Career career) {
        logger.info("Saving new career listing: {}", career.getTitle());
        Career savedCareer = careerRepository.save(career);
        logger.info("Career listing saved with id: {}", savedCareer.getId());
        return savedCareer;
    }

    // Initialize default career listings if database is empty
    public void initializeDefaultCareers() {
        if (careerRepository.count() == 0) {
            logger.info("No career listings found, initializing default listings");
            
            List<Career> defaultCareers = Arrays.asList(
                new Career(
                    "Web Development Intern",
                    "internship",
                    "web",
                    "Remote",
                    "3 months",
                    "Join our web development team and work on exciting projects using modern technologies.",
                    Arrays.asList("HTML/CSS", "JavaScript", "React", "Node.js")
                ),
                new Career(
                    "Mobile App Development Intern",
                    "internship",
                    "mobile",
                    "Remote",
                    "3 months",
                    "Help us build innovative mobile applications for iOS and Android platforms.",
                    Arrays.asList("Flutter", "React Native", "Firebase", "REST APIs")
                ),
                new Career(
                    "Full Stack Development Intern",
                    "internship",
                    "web",
                    "Remote",
                    "3 months",
                    "Work on end-to-end development of web applications, from frontend to backend. Gain experience in building scalable and robust applications.",
                    Arrays.asList("React", "Node.js", "MongoDB", "Express.js", "TypeScript", "AWS")
                )
            );

            careerRepository.saveAll(defaultCareers);
            logger.info("Default career listings initialized successfully");
        } else {
            logger.info("Career listings already exist in database, skipping initialization");
        }
    }
} 