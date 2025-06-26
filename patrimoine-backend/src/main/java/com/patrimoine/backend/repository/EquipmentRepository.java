package com.patrimoine.backend.repository;

import com.patrimoine.backend.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByVilleCentreIgnoreCase(String villeCentre);
    List<Equipment> findByVilleCentreIgnoreCaseAndValidatedTrue(String villeCentre);
    List<Equipment> findByValidatedFalse();
    List<Equipment> findByAddedBy(String email);
    List<Equipment> findByValidatedTrue();
    List<Equipment> findByVilleCentreIgnoreCaseAndStatus(String villeCentre, String status);
    List<Equipment> findByEnMaintenance(boolean enMaintenance);
    List<Equipment> findByVilleCentreIgnoreCaseAndEnMaintenance(String villeCentre, boolean enMaintenance);

    @Modifying
    @Query("UPDATE Equipment e SET e.enMaintenance = :status WHERE e.id = :id")
    void updateMaintenanceStatus(@Param("id") Long id, @Param("status") boolean status);

    // Méthodes de comptage existantes
    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.validated = :validated")
    long countByValidated(@Param("validated") boolean validated);

    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.enMaintenance = :status")
    long countByEnMaintenance(@Param("status") boolean status);

    // Méthodes manquantes à ajouter
    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.status = :status")
    long countByStatus(@Param("status") String status);

    // Méthodes dérivées (générées automatiquement par Spring Data JPA)
    long countByValidatedTrue();

    long countByValidatedFalse();
}