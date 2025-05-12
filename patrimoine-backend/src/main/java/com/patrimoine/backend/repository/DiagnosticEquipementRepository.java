package com.patrimoine.backend.repository;

import com.patrimoine.backend.entity.DiagnosticEquipement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiagnosticEquipementRepository extends JpaRepository<DiagnosticEquipement, Long> {
    List<DiagnosticEquipement> findByVilleCentre(String villeCentre);
    List<DiagnosticEquipement> findByBesoinMaintenance(boolean besoinMaintenance);
    List<DiagnosticEquipement> findByVilleCentreAndBesoinMaintenance(String villeCentre, boolean besoinMaintenance);
    List<DiagnosticEquipement> findByMaintenanceEffectuee(boolean maintenanceEffectuee);
    boolean existsByIdEquipement(String idEquipement); // ← Add this line
}
