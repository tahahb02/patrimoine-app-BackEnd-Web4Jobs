
package com.patrimoine.backend.service;

import com.patrimoine.backend.entity.MaintenanceEquipement;
import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.repository.MaintenanceEquipementRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceEquipementService {

    private final MaintenanceEquipementRepository maintenanceRepository;

    public MaintenanceEquipementService(MaintenanceEquipementRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    public MaintenanceEquipement creerMaintenance(String idEquipement, String nomEquipement, String categorie,
                                                  String villeCentre, String typeProbleme, String description,
                                                  int dureeEstimee, Utilisateur technicien) {
        MaintenanceEquipement maintenance = new MaintenanceEquipement();
        maintenance.setIdEquipement(idEquipement);
        maintenance.setNomEquipement(nomEquipement);
        maintenance.setCategorie(categorie);
        maintenance.setVilleCentre(villeCentre);
        maintenance.setTypeProbleme(MaintenanceEquipement.TypeProbleme.valueOf(typeProbleme));
        maintenance.setDescriptionProbleme(description);
        maintenance.setDureeReelle(dureeEstimee);
        maintenance.setDateDebut(LocalDateTime.now());
        maintenance.setTechnicien(technicien);
        maintenance.setTermine(false);

        return maintenanceRepository.save(maintenance);
    }

    public MaintenanceEquipement terminerMaintenance(Long idMaintenance, String actionsRealisees, int dureeReelle) {
        return maintenanceRepository.findById(idMaintenance).map(maintenance -> {
            maintenance.setActionsRealisees(actionsRealisees);
            maintenance.setDureeReelle(dureeReelle);
            maintenance.setDateFin(LocalDateTime.now());
            maintenance.setTermine(true);
            return maintenanceRepository.save(maintenance);
        }).orElseThrow(() -> new RuntimeException("Maintenance non trouvée"));
    }

    public List<MaintenanceEquipement> getMaintenancesByVilleCentre(String villeCentre) {
        return maintenanceRepository.findByVilleCentre(villeCentre);
    }

    public List<MaintenanceEquipement> getMaintenancesEnCours(String villeCentre) {
        return maintenanceRepository.findByVilleCentreAndTermine(villeCentre, false);
    }

    public List<MaintenanceEquipement> getHistoriqueMaintenances(String villeCentre) {
        return maintenanceRepository.findByVilleCentreAndTermine(villeCentre, true);
    }

    public List<MaintenanceEquipement> getMaintenancesByTechnicien(Long technicienId) {
        return maintenanceRepository.findByTechnicienId(technicienId);
    }
}