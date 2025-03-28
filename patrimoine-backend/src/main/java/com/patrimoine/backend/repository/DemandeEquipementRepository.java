package com.patrimoine.backend.repository;

import com.patrimoine.backend.entity.DemandeEquipement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DemandeEquipementRepository extends JpaRepository<DemandeEquipement, Long> {

    List<DemandeEquipement> findByUtilisateurId(Long utilisateurId);

    List<DemandeEquipement> findByStatut(String statut);

    List<DemandeEquipement> findByStatutNot(String statut);

    @Query("SELECT d FROM DemandeEquipement d WHERE " +
            "(:nom IS NULL OR d.nom LIKE %:nom%) AND " +
            "(:prenom IS NULL OR d.prenom LIKE %:prenom%) AND " +
            "(:centre IS NULL OR d.centreEquipement LIKE %:centre%)")
    List<DemandeEquipement> filtrerDemandes(
            @Param("nom") String nom,
            @Param("prenom") String prenom,
            @Param("centre") String centre);
}