package com.SarankarDeveloperWebsite.repository;

import com.SarankarDeveloperWebsite.model.Career;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CareerRepository extends MongoRepository<Career, String> {
    List<Career> findByCategory(String category);
    List<Career> findByType(String type);
} 