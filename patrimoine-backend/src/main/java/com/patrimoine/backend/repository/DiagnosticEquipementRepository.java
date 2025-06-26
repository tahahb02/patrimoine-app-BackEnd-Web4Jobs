package com.patrimoine.backend.repository;

import com.patrimoine.backend.entity.DiagnosticEquipement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiagnosticEquipementRepository extends JpaRepository<DiagnosticEquipement, Long> {
    List<DiagnosticEquipement> findByVilleCentre(String villeCentre);
    List<DiagnosticEquipement> findByBesoinMaintenance(boolean besoinMaintenance);
    List<DiagnosticEquipement> findByVilleCentreAndBesoinMaintenance(String villeCentre, boolean besoinMaintenance);
    List<DiagnosticEquipement> findByMaintenanceEffectuee(boolean maintenanceEffectuee);
    boolean existsByIdEquipement(String idEquipement);

    @Query("SELECT COUNT(d) FROM DiagnosticEquipement d WHERE d.besoinMaintenance = :besoinMaintenance")
    long countByBesoinMaintenance(@Param("besoinMaintenance") boolean besoinMaintenance);

    @Query("SELECT COUNT(d) FROM DiagnosticEquipement d WHERE d.maintenanceEffectuee = :maintenanceEffectuee")
    long countByMaintenanceEffectuee(@Param("maintenanceEffectuee") boolean maintenanceEffectuee);// ← Add this line
}
