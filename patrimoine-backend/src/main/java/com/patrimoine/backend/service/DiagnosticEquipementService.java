
package com.patrimoine.backend.service;

import com.patrimoine.backend.entity.DemandeEquipement;
import com.patrimoine.backend.entity.DiagnosticEquipement;
import com.patrimoine.backend.entity.Equipment;
import com.patrimoine.backend.repository.DemandeEquipementRepository;
import com.patrimoine.backend.repository.DiagnosticEquipementRepository;
import com.patrimoine.backend.repository.EquipmentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiagnosticEquipementService {

    private final EquipmentRepository equipmentRepository;
    private final DemandeEquipementRepository demandeEquipementRepository;
    private final DiagnosticEquipementRepository diagnosticEquipementRepository;

    public DiagnosticEquipementService(EquipmentRepository equipmentRepository,
                                       DemandeEquipementRepository demandeEquipementRepository,
                                       DiagnosticEquipementRepository diagnosticEquipementRepository) {
        this.equipmentRepository = equipmentRepository;
        this.demandeEquipementRepository = demandeEquipementRepository;
        this.diagnosticEquipementRepository = diagnosticEquipementRepository;
    }

    @Scheduled(cron = "0 0 9 * * ?") // Exécuté tous les jours à 9h
    public void verifierEquipementsPourDiagnostic() {
        List<Equipment> equipements = equipmentRepository.findAll();

        for (Equipment equipement : equipements) {
            // Vérifier les heures d'utilisation
            List<DemandeEquipement> demandes = demandeEquipementRepository.findByNomEquipementAndStatut(equipement.getName(), "RETOURNEE");
            long totalHeures = demandes.stream()
                    .mapToLong(d -> d.getDureeUtilisation() != null ? d.getDureeUtilisation() : 0)
                    .sum();

            int seuilHeures = "PC".equalsIgnoreCase(equipement.getCategory()) ? 1000 : 1500;
            int seuilDemandes = "PC".equalsIgnoreCase(equipement.getCategory()) ? 20 : 40;

            if (totalHeures >= seuilHeures || demandes.size() >= seuilDemandes) {
                // Vérifier si un diagnostic n'existe pas déjà
                boolean diagnosticExiste = diagnosticEquipementRepository.existsByIdEquipement(equipement.getId().toString());

                if (!diagnosticExiste) {
                    DiagnosticEquipement diagnostic = new DiagnosticEquipement();
                    diagnostic.setIdEquipement(equipement.getId().toString());
                    diagnostic.setNomEquipement(equipement.getName());
                    diagnostic.setCategorie(equipement.getCategory());
                    diagnostic.setVilleCentre(equipement.getVilleCentre());
                    diagnostic.setDateDiagnostic(LocalDateTime.now());
                    diagnostic.setBesoinMaintenance(false);
                    diagnostic.setMaintenanceEffectuee(false);

                    diagnosticEquipementRepository.save(diagnostic);
                }
            }
        }
    }

    public List<DiagnosticEquipement> getDiagnosticsByVilleCentre(String villeCentre) {
        return diagnosticEquipementRepository.findByVilleCentre(villeCentre);
    }

    public List<DiagnosticEquipement> getDiagnosticsBesoinMaintenance(String villeCentre) {
        return diagnosticEquipementRepository.findByVilleCentreAndBesoinMaintenance(villeCentre, true);
    }

    public DiagnosticEquipement updateDiagnostic(Long id, boolean besoinMaintenance, String typeProbleme,
                                                 String degreUrgence, String description, int dureeEstimee) {
        return diagnosticEquipementRepository.findById(id).map(diagnostic -> {
            diagnostic.setBesoinMaintenance(besoinMaintenance);
            if (besoinMaintenance) {
                diagnostic.setTypeProbleme(DiagnosticEquipement.TypeProbleme.valueOf(typeProbleme));
                diagnostic.setDegreUrgence(DiagnosticEquipement.DegreUrgence.valueOf(degreUrgence));
                diagnostic.setDescriptionProbleme(description);
                diagnostic.setDureeEstimee(dureeEstimee);
            }
            return diagnosticEquipementRepository.save(diagnostic);
        }).orElseThrow(() -> new RuntimeException("Diagnostic non trouvé"));
    }

    public void marquerMaintenanceEffectuee(Long idDiagnostic) {
        diagnosticEquipementRepository.findById(idDiagnostic).ifPresent(diagnostic -> {
            diagnostic.setMaintenanceEffectuee(true);
            diagnosticEquipementRepository.save(diagnostic);
        });
    }

    public DiagnosticEquipement saveDiagnostic(DiagnosticEquipement diagnostic) {
        return diagnosticEquipementRepository.save(diagnostic);
    }

    public List<DiagnosticEquipement> getAllDiagnostics() {
        return diagnosticEquipementRepository.findAll();
    }
}