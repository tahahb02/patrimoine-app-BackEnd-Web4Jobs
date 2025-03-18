package com.patrimoine.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipments")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String center;

    @Column(nullable = false)
    private String dateAdded;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String imageUrl; // Champ pour stocker l'URL de l'image

    // Constructeurs, getters et setters
    public Equipment() {}

    public Equipment(String name, String category, String center, String dateAdded, String description, String imageUrl) {
        this.name = name;
        this.category = category;
        this.center = center;
        this.dateAdded = dateAdded;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCenter() { return center; }
    public void setCenter(String center) { this.center = center; }

    public String getDateAdded() { return dateAdded; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}