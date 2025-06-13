package com.SarankarDeveloperWebsite.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "careers")
public class Career {
    @Id
    private String id;
    private String title;
    private String type;
    private String category;
    private String location;
    private String duration;
    private String description;
    private List<String> skills;

    // Default constructor
    public Career() {}

    // Constructor with all fields
    public Career(String title, String type, String category, String location, 
                 String duration, String description, List<String> skills) {
        this.title = title;
        this.type = type;
        this.category = category;
        this.location = location;
        this.duration = duration;
        this.description = description;
        this.skills = skills;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
} 