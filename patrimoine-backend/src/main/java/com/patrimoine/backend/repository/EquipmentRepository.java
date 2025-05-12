package com.patrimoine.backend.repository;

import com.patrimoine.backend.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
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

}