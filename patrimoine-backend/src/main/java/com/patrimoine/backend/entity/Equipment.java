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

    private String dateAdded; // Utiliser String

    // Constructeurs
    public Equipment() {}

    public Equipment(String name, String category, String center, String dateAdded) {
        this.name = name;
        this.category = category;
        this.center = center;
        this.dateAdded = dateAdded;
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
}