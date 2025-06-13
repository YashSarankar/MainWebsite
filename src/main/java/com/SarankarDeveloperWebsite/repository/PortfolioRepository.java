package com.SarankarDeveloperWebsite.repository;

import com.SarankarDeveloperWebsite.model.PortfolioItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PortfolioRepository extends MongoRepository<PortfolioItem, String> {
    List<PortfolioItem> findByCategory(String category);
} 