package com.patrimoine.backend.repository;

import com.patrimoine.backend.entity.DemandeEquipement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DemandeEquipementRepository extends JpaRepository<DemandeEquipement, Long> {
    List<DemandeEquipement> findByUtilisateurId(Long userId);
    List<DemandeEquipement> findByStatut(String statut);
    List<DemandeEquipement> findByStatutNot(String statut);

    @Query("SELECT d FROM DemandeEquipement d WHERE d.urgence = 'ELEVEE'")
    List<DemandeEquipement> findDemandesUrgentes();

    @Query("SELECT d FROM DemandeEquipement d WHERE d.dateFin < CURRENT_TIMESTAMP AND d.statut = 'EN_ATTENTE'")
    List<DemandeEquipement> findDemandesEnRetard();

    @Query("SELECT d FROM DemandeEquipement d WHERE d.idEquipement = :equipementId AND d.statut = 'ACCEPTEE'")
    List<DemandeEquipement> findDemandesAccepteesParEquipement(String equipementId);

    @Query("SELECT d.utilisateur, SUM(d.dureeUtilisation) as totalHeures " +
            "FROM DemandeEquipement d " +
            "WHERE d.idEquipement = :equipementId AND d.statut = 'ACCEPTEE' " +
            "GROUP BY d.utilisateur")
    List<Object[]> findUtilisationParAdherant(String equipementId);

    @Query("SELECT d FROM DemandeEquipement d WHERE d.dateDebut BETWEEN :start AND :end AND d.statut = :statut")
    List<DemandeEquipement> findByDateDebutBetweenAndStatut(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("statut") String statut);

    @Query("SELECT d FROM DemandeEquipement d WHERE d.dateFin BETWEEN :start AND :end AND d.statut = :statut")
    List<DemandeEquipement> findByDateFinBetweenAndStatut(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("statut") String statut);
}