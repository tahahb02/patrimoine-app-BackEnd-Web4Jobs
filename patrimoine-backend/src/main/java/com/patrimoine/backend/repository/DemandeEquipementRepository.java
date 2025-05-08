    package com.patrimoine.backend.repository;

    import com.patrimoine.backend.entity.DemandeEquipement;
    import com.patrimoine.backend.entity.Utilisateur;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import java.time.LocalDateTime;
    import java.util.List;

    public interface DemandeEquipementRepository extends JpaRepository<DemandeEquipement, Long> {

        @Query("SELECT d FROM DemandeEquipement d WHERE d.utilisateur.id = :userId ORDER BY d.dateDemande DESC")
        List<DemandeEquipement> findByUtilisateurId(Long userId);
        List<DemandeEquipement> findByStatut(String statut);
        List<DemandeEquipement> findByStatutNot(String statut);
        List<DemandeEquipement> findByStatutNotAndVilleCentre(String statut, String villeCentre);


        @Query("SELECT d FROM DemandeEquipement d WHERE d.urgence = 'ELEVEE' AND d.villeCentre = :villeCentre")
        List<DemandeEquipement> findDemandesUrgentesByVilleCentre(@Param("villeCentre") String villeCentre);

        @Query("SELECT d FROM DemandeEquipement d WHERE d.dateFin < CURRENT_TIMESTAMP AND d.statut = 'EN_ATTENTE' AND d.villeCentre = :villeCentre")
        List<DemandeEquipement> findDemandesEnRetardByVilleCentre(@Param("villeCentre") String villeCentre);

        @Query("SELECT d FROM DemandeEquipement d WHERE d.idEquipement = :equipementId AND d.statut = 'ACCEPTEE'")
        List<DemandeEquipement> findDemandesAccepteesParEquipement(@Param("equipementId") String equipementId);

        @Query("SELECT d FROM DemandeEquipement d WHERE d.statut = 'EN_ATTENTE' AND d.villeCentre = :villeCentre")
        List<DemandeEquipement> findDemandesEnAttenteByVilleCentre(@Param("villeCentre") String villeCentre);

        @Query("SELECT d FROM DemandeEquipement d WHERE d.villeCentre = :villeCentre")
        List<DemandeEquipement> findByVilleCentre(@Param("villeCentre") String villeCentre);

        @Query("SELECT d FROM DemandeEquipement d WHERE d.dateDebut BETWEEN :start AND :end AND d.statut = :statut AND d.villeCentre = :villeCentre")
        List<DemandeEquipement> findByDateDebutBetweenAndStatutAndVilleCentre(
                @Param("start") LocalDateTime start,
                @Param("end") LocalDateTime end,
                @Param("statut") String statut,
                @Param("villeCentre") String villeCentre);

        @Query("SELECT d FROM DemandeEquipement d WHERE d.dateFin BETWEEN :start AND :end AND d.statut = :statut AND d.villeCentre = :villeCentre")
        List<DemandeEquipement> findByDateFinBetweenAndStatutAndVilleCentre(
                @Param("start") LocalDateTime start,
                @Param("end") LocalDateTime end,
                @Param("statut") String statut,
                @Param("villeCentre") String villeCentre);

        @Query("SELECT d.utilisateur, SUM(d.dureeUtilisation) as totalHeures " +
                "FROM DemandeEquipement d " +
                "WHERE d.idEquipement = :equipementId AND d.statut = 'ACCEPTEE' " +
                "GROUP BY d.utilisateur")
        List<Object[]> findUtilisationParAdherant(@Param("equipementId") String equipementId);

        @Query("SELECT d FROM DemandeEquipement d WHERE d.utilisateur.id = :userId ORDER BY d.dateDemande DESC")
        List<DemandeEquipement> findByUtilisateurIdOrderByDateDemandeDesc(@Param("userId") Long userId);

        @Query("SELECT d FROM DemandeEquipement d ORDER BY d.dateDemande DESC")
        List<DemandeEquipement> findAllByOrderByDateDemandeDesc();
    }