package com.patrimoine.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "demande_id")
    private DemandeEquipement demande;

    private String type; // "FEEDBACK", "DEMANDE", "REPONSE"
    private String titre;
    private String message;
    private LocalDateTime dateCreation;
    private boolean lue;
    private String link;
    private String equipmentId;
    private String equipmentName;
    private Long relatedId;

    // Nouveaux champs
    private String demandeurNom;
    private String demandeurPrenom;
    private String statutDemande;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public DemandeEquipement getDemande() { return demande; }
    public void setDemande(DemandeEquipement demande) { this.demande = demande; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public boolean isLue() { return lue; }
    public void setLue(boolean lue) { this.lue = lue; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public String getEquipmentId() { return equipmentId; }
    public void setEquipmentId(String equipmentId) { this.equipmentId = equipmentId; }
    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }
    public String getDemandeurNom() { return demandeurNom; }
    public void setDemandeurNom(String demandeurNom) { this.demandeurNom = demandeurNom; }
    public String getDemandeurPrenom() { return demandeurPrenom; }
    public void setDemandeurPrenom(String demandeurPrenom) { this.demandeurPrenom = demandeurPrenom; }
    public String getStatutDemande() { return statutDemande; }
    public void setStatutDemande(String statutDemande) { this.statutDemande = statutDemande; }

    @PrePersist
    public void prePersist() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
    }
}