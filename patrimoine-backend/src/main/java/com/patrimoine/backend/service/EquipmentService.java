package com.patrimoine.backend.service;

import com.patrimoine.backend.entity.Equipment;
import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.entity.DemandeEquipement;
import com.patrimoine.backend.repository.EquipmentRepository;
import com.patrimoine.backend.repository.DemandeEquipementRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final DemandeEquipementRepository demandeEquipementRepository;

    public EquipmentService(EquipmentRepository equipmentRepository,
                            DemandeEquipementRepository demandeEquipementRepository) {
        this.equipmentRepository = equipmentRepository;
        this.demandeEquipementRepository = demandeEquipementRepository;
    }

    public Equipment addEquipment(Equipment equipment) {
        equipment.setEnMaintenance(false); // Initialisation explicite
        return equipmentRepository.save(equipment);
    }

    public List<Equipment> getAllEquipments() {
        return equipmentRepository.findAll().stream()
                .map(this::normalizeEquipment)
                .collect(Collectors.toList());
    }

    public List<Equipment> getValidatedEquipments() {
        return equipmentRepository.findByValidatedTrue().stream()
                .map(this::normalizeEquipment)
                .collect(Collectors.toList());
    }

    public List<Equipment> getValidatedEquipmentsByCenter(String villeCentre) {
        return equipmentRepository.findByVilleCentreIgnoreCaseAndValidatedTrue(villeCentre).stream()
                .map(this::normalizeEquipment)
                .collect(Collectors.toList());
    }

    public List<Equipment> getPendingEquipments() {
        return equipmentRepository.findByValidatedFalse().stream()
                .map(this::normalizeEquipment)
                .collect(Collectors.toList());
    }

    public List<Equipment> getEquipmentsByAddedBy(String email) {
        return equipmentRepository.findByAddedBy(email).stream()
                .map(this::normalizeEquipment)
                .collect(Collectors.toList());
    }

    public Optional<Equipment> getEquipmentById(Long id) {
        return equipmentRepository.findById(id)
                .map(this::normalizeEquipment);
    }

    public Optional<Equipment> updateEquipment(Long id, Equipment updatedEquipment) {
        return equipmentRepository.findById(id).map(existingEquipment -> {
            existingEquipment.setName(updatedEquipment.getName());
            existingEquipment.setCategory(updatedEquipment.getCategory());
            existingEquipment.setDescription(updatedEquipment.getDescription());
            existingEquipment.setImageUrl(updatedEquipment.getImageUrl());
            existingEquipment.setStatus(updatedEquipment.getStatus());
            existingEquipment.setEnMaintenance(updatedEquipment.isEnMaintenance());
            return equipmentRepository.save(existingEquipment);
        });
    }

    public Optional<Equipment> validateEquipment(Long id) {
        return equipmentRepository.findById(id).map(equipment -> {
            equipment.setValidated(true);
            return equipmentRepository.save(equipment);
        });
    }

    public boolean deleteEquipment(Long id) {
        if (equipmentRepository.existsById(id)) {
            equipmentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Equipment> getAvailableEquipmentsByCenter(String villeCentre) {
        return equipmentRepository.findByVilleCentreIgnoreCaseAndValidatedTrue(villeCentre).stream()
                .filter(equip -> !equip.isEnMaintenance() &&
                        (equip.getStatus() == null || equip.getStatus().equalsIgnoreCase("Disponible")))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getEquipmentHistory(Long equipmentId) {
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(equipmentId);
        if (equipmentOpt.isEmpty()) {
            return Collections.emptyMap();
        }

        Equipment equipment = equipmentOpt.get();
        List<DemandeEquipement> demandes = demandeEquipementRepository.findByNomEquipementAndStatut(equipment.getName(), "RETOURNEE");

        Map<String, Object> history = new HashMap<>();
        List<Map<String, Object>> utilisations = new ArrayList<>();
        long totalHeures = 0;

        for (DemandeEquipement demande : demandes) {
            Map<String, Object> utilisation = new HashMap<>();
            Utilisateur user = demande.getUtilisateur();

            utilisation.put("nom", user.getNom());
            utilisation.put("prenom", user.getPrenom());
            utilisation.put("email", user.getEmail());
            utilisation.put("telephone", user.getPhone());
            utilisation.put("villeCentre", demande.getVilleCentre());

            long duree = demande.getDureeUtilisation() != null ? demande.getDureeUtilisation() : 0;
            utilisation.put("heuresUtilisation", duree);
            totalHeures += duree;

            utilisations.add(utilisation);
        }

        history.put("equipmentId", equipment.getId());
        history.put("equipmentName", equipment.getName());
        history.put("villeCentre", equipment.getVilleCentre());
        history.put("category", equipment.getCategory());
        history.put("utilisations", utilisations);
        history.put("totalUtilisations", demandes.size());
        history.put("totalHeures", totalHeures);

        return history;
    }

    public List<String> getAllCenters() {
        return Arrays.stream(Utilisateur.VilleCentre.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public List<Equipment> findByVilleCentreIgnoreCaseAndStatus(String villeCentre, String status) {
        return equipmentRepository.findByVilleCentreIgnoreCaseAndStatus(villeCentre, status).stream()
                .map(this::normalizeEquipment)
                .collect(Collectors.toList());
    }

    public Optional<Equipment> mettreEnMaintenance(Long id) {
        return equipmentRepository.findById(id).map(equipment -> {
            equipment.setEnMaintenance(true);
            return equipmentRepository.save(equipment);
        });
    }

    public Optional<Equipment> sortirDeMaintenance(Long id) {
        return equipmentRepository.findById(id).map(equipment -> {
            equipment.setEnMaintenance(false);
            return equipmentRepository.save(equipment);
        });
    }

    public List<Equipment> getEquipementsEnMaintenance() {
        return equipmentRepository.findByEnMaintenance(true).stream()
                .map(this::normalizeEquipment)
                .collect(Collectors.toList());
    }

    public List<Equipment> getEquipementsEnMaintenanceByCentre(String villeCentre) {
        return equipmentRepository.findByVilleCentreIgnoreCaseAndEnMaintenance(villeCentre, true).stream()
                .map(this::normalizeEquipment)
                .collect(Collectors.toList());
    }

    private Equipment normalizeEquipment(Equipment equipment) {
        if (equipment.isEnMaintenance() == null) {
            equipment.setEnMaintenance(false);
        }
        if (equipment.getStatus() == null) {
            equipment.setStatus("Disponible");
        }
        return equipment;
    }
}