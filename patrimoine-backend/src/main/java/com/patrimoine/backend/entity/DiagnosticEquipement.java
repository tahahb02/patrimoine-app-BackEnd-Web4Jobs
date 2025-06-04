
package com.patrimoine.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DiagnosticEquipement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idEquipement;
    private String nomEquipement;
    private String categorie;
    private String villeCentre;

    @Enumerated(EnumType.STRING)
    private TypeProbleme typeProbleme;

    @Enumerated(EnumType.STRING)
    private DegreUrgence degreUrgence;

    private String descriptionProbleme;
    private int dureeEstimee; // en heures
    private LocalDateTime dateDiagnostic;
    private boolean besoinMaintenance;
    private boolean maintenanceEffectuee;
    @Column(name = "automatic_diagnostic", nullable = false, columnDefinition = "boolean default false")
    private boolean automaticDiagnostic = false; // Valeur par défaut

    public enum TypeProbleme {
        MATERIEL,
        LOGICIEL
    }

    public enum DegreUrgence {
        FAIBLE,
        MOYEN,
        ELEVE
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
    public DegreUrgence getDegreUrgence() { return degreUrgence; }
    public void setDegreUrgence(DegreUrgence degreUrgence) { this.degreUrgence = degreUrgence; }
    public String getDescriptionProbleme() { return descriptionProbleme; }
    public void setDescriptionProbleme(String descriptionProbleme) { this.descriptionProbleme = descriptionProbleme; }
    public int getDureeEstimee() { return dureeEstimee; }
    public void setDureeEstimee(int dureeEstimee) { this.dureeEstimee = dureeEstimee; }
    public LocalDateTime getDateDiagnostic() { return dateDiagnostic; }
    public void setDateDiagnostic(LocalDateTime dateDiagnostic) { this.dateDiagnostic = dateDiagnostic; }
    public boolean isBesoinMaintenance() { return besoinMaintenance; }
    public void setBesoinMaintenance(boolean besoinMaintenance) { this.besoinMaintenance = besoinMaintenance; }
    public boolean isMaintenanceEffectuee() { return maintenanceEffectuee; }
    public void setMaintenanceEffectuee(boolean maintenanceEffectuee) { this.maintenanceEffectuee = maintenanceEffectuee; }
    public boolean isAutomaticDiagnostic() {return automaticDiagnostic;}
    public void setAutomaticDiagnostic(boolean automaticDiagnostic) {this.automaticDiagnostic = automaticDiagnostic;}
}

