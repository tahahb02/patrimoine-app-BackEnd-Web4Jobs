    package com.patrimoine.backend.entity;

    import jakarta.persistence.*;
    import java.time.LocalDateTime;
    import java.util.List;

    @Entity
    public class Feedback {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "utilisateur_id", nullable = false)
        private Utilisateur utilisateur;

        @ManyToOne
        @JoinColumn(name = "demande_id", nullable = false)
        private DemandeEquipement demande;

        private String equipmentId;
        private String equipmentName;
        private LocalDateTime dateFeedback;
        private int satisfaction;
        private int performance;
        private int faciliteUtilisation;
        private int fiabilite;
        private String commentaires;
        private String problemesRencontres;

        @ElementCollection
        @CollectionTable(name = "feedback_problemes_techniques", joinColumns = @JoinColumn(name = "feedback_id"))
        @Column(name = "probleme")
        private List<String> problemesTechniques;

        private String recommander;
        private String email;
        private String villeCentre;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Utilisateur getUtilisateur() { return utilisateur; }
        public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
        public DemandeEquipement getDemande() { return demande; }
        public void setDemande(DemandeEquipement demande) { this.demande = demande; }
        public String getEquipmentId() { return equipmentId; }
        public void setEquipmentId(String equipmentId) { this.equipmentId = equipmentId; }
        public String getEquipmentName() { return equipmentName; }
        public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
        public LocalDateTime getDateFeedback() { return dateFeedback; }
        public void setDateFeedback(LocalDateTime dateFeedback) { this.dateFeedback = dateFeedback; }
        public int getSatisfaction() { return satisfaction; }
        public void setSatisfaction(int satisfaction) { this.satisfaction = satisfaction; }
        public int getPerformance() { return performance; }
        public void setPerformance(int performance) { this.performance = performance; }
        public int getFaciliteUtilisation() { return faciliteUtilisation; }
        public void setFaciliteUtilisation(int faciliteUtilisation) { this.faciliteUtilisation = faciliteUtilisation; }
        public int getFiabilite() { return fiabilite; }
        public void setFiabilite(int fiabilite) { this.fiabilite = fiabilite; }
        public String getCommentaires() { return commentaires; }
        public void setCommentaires(String commentaires) { this.commentaires = commentaires; }
        public String getProblemesRencontres() { return problemesRencontres; }
        public void setProblemesRencontres(String problemesRencontres) { this.problemesRencontres = problemesRencontres; }
        public List<String> getProblemesTechniques() { return problemesTechniques; }
        public void setProblemesTechniques(List<String> problemesTechniques) { this.problemesTechniques = problemesTechniques; }
        public String getRecommander() { return recommander; }
        public void setRecommander(String recommander) { this.recommander = recommander; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getVilleCentre() { return villeCentre; }
        public void setVilleCentre(String villeCentre) { this.villeCentre = villeCentre; }

        @PrePersist
        public void prePersist() {
            this.dateFeedback = LocalDateTime.now();
        }
    }