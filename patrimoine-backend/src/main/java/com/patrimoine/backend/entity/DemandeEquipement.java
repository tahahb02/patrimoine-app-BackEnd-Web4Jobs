package com.patrimoine.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "demandes_equipement")
public class DemandeEquipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_equipement", nullable = false)
    private String idEquipement;

    @Column(name = "nom_equipement", nullable = false)
    private String nomEquipement;

    @Column(name = "categorie_equipement", nullable = false)
    private String categorieEquipement;

    @Column(name = "centre_equipement", nullable = false)
    private String centreEquipement;

    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "numero_telephone", nullable = false)
    private String numeroTelephone;

    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;

    @Column(name = "remarques", columnDefinition = "TEXT")
    private String remarques;

    @Column(name = "statut")
    private String statut = "EN_ATTENTE";

    @Column(name = "commentaire_responsable", columnDefinition = "TEXT")
    private String commentaireResponsable;


    @ManyToOne
    @JoinColumn(name = "adherant_id") // Modification ici pour correspondre à la base de données
    private Utilisateur utilisateur;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIdEquipement() { return idEquipement; }
    public void setIdEquipement(String idEquipement) { this.idEquipement = idEquipement; }
    public String getNomEquipement() { return nomEquipement; }
    public void setNomEquipement(String nomEquipement) { this.nomEquipement = nomEquipement; }
    public String getCategorieEquipement() { return categorieEquipement; }
    public void setCategorieEquipement(String categorieEquipement) { this.categorieEquipement = categorieEquipement; }
    public String getCentreEquipement() { return centreEquipement; }
    public void setCentreEquipement(String centreEquipement) { this.centreEquipement = centreEquipement; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getNumeroTelephone() { return numeroTelephone; }
    public void setNumeroTelephone(String numeroTelephone) { this.numeroTelephone = numeroTelephone; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    public String getRemarques() { return remarques; }
    public void setRemarques(String remarques) { this.remarques = remarques; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getCommentaireResponsable() { return commentaireResponsable; }
    public void setCommentaireResponsable(String commentaireResponsable) { this.commentaireResponsable = commentaireResponsable; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
}