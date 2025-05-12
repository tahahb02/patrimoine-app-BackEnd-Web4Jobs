
package com.patrimoine.backend.repository;

import com.patrimoine.backend.entity.MaintenanceEquipement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaintenanceEquipementRepository extends JpaRepository<MaintenanceEquipement, Long> {
    List<MaintenanceEquipement> findByVilleCentre(String villeCentre);
    List<MaintenanceEquipement> findByTermine(boolean termine);
    List<MaintenanceEquipement> findByVilleCentreAndTermine(String villeCentre, boolean termine);
    List<MaintenanceEquipement> findByTechnicienId(Long technicienId);
}