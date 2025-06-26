
package com.patrimoine.backend.repository;

import com.patrimoine.backend.entity.MaintenanceEquipement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaintenanceEquipementRepository extends JpaRepository<MaintenanceEquipement, Long> {
    List<MaintenanceEquipement> findByVilleCentre(String villeCentre);
    List<MaintenanceEquipement> findByTermine(boolean termine);
    List<MaintenanceEquipement> findByVilleCentreAndTermine(String villeCentre, boolean termine);
    List<MaintenanceEquipement> findByTechnicienId(Long technicienId);

    @Query("SELECT COUNT(m) FROM MaintenanceEquipement m WHERE m.termine = :termine")
    long countByTermine(@Param("termine") boolean termine);
}