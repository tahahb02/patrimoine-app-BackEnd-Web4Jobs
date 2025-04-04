package com.patrimoine.backend.repository;

import com.patrimoine.backend.entity.DemandeEquipement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface DemandeEquipementRepository extends JpaRepository<DemandeEquipement, Long> {
    List<DemandeEquipement> findByUtilisateurId(Long userId);
    List<DemandeEquipement> findByStatut(String statut);
    List<DemandeEquipement> findByStatutNot(String statut);

    @Query("SELECT d FROM DemandeEquipement d WHERE d.urgence = 'ELEVEE'")
    List<DemandeEquipement> findDemandesUrgentes();

    @Query("SELECT d FROM DemandeEquipement d WHERE d.dateFin < CURRENT_TIMESTAMP AND d.statut = 'EN_ATTENTE'")
    List<DemandeEquipement> findDemandesEnRetard();
}