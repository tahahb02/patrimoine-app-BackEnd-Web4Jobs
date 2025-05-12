
package com.patrimoine.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class MaintenanceEquipement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idEquipement;
    private String nomEquipement;
    private String categorie;
    private String villeCentre;

    @Enumerated(EnumType.STRING)
    private TypeProbleme typeProbleme;

    private String descriptionProbleme;
    private String actionsRealisees;
    private int dureeReelle; // en heures
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private boolean termine;

    @ManyToOne
    private Utilisateur technicien;

    public enum TypeProbleme {
        MATERIEL,
        LOGICIEL
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIdEquipement() { return idEquipement; }
    public void setIdEquipement(String idEquipement) { this.idEquipement = idEquipement; }
    public String getNomEquipement() { return nomEquipement; }
    public void setNomEquipement(String nomEquipement) { this.nomEquipement = nomEquipement; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getVilleCentre() { return villeCentre; }
    public void setVilleCentre(String villeCentre) { this.villeCentre = villeCentre; }
    public TypeProbleme getTypeProbleme() { return typeProbleme; }
    public void setTypeProbleme(TypeProbleme typeProbleme) { this.typeProbleme = typeProbleme; }
    public String getDescriptionProbleme() { return descriptionProbleme; }
    public void setDescriptionProbleme(String descriptionProbleme) { this.descriptionProbleme = descriptionProbleme; }
    public String getActionsRealisees() { return actionsRealisees; }
    public void setActionsRealisees(String actionsRealisees) { this.actionsRealisees = actionsRealisees; }
    public int getDureeReelle() { return dureeReelle; }
    public void setDureeReelle(int dureeReelle) { this.dureeReelle = dureeReelle; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    public boolean isTermine() { return termine; }
    public void setTermine(boolean termine) { this.termine = termine; }
    public Utilisateur getTechnicien() { return technicien; }
    public void setTechnicien(Utilisateur technicien) { this.technicien = technicien; }
}