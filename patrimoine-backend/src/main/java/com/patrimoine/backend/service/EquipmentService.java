package com.patrimoine.backend.service;

import com.patrimoine.backend.entity.Equipment;
import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.repository.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public Equipment addEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    public List<Equipment> getAllEquipments() {
        return equipmentRepository.findAll();
    }

    public List<Equipment> getValidatedEquipments() {
        return equipmentRepository.findByValidatedTrue();
    }

    public List<Equipment> getValidatedEquipmentsByCenter(String villeCentre) {
        return equipmentRepository.findByVilleCentreIgnoreCaseAndValidatedTrue(villeCentre);
    }

    public List<Equipment> getPendingEquipments() {
        return equipmentRepository.findByValidatedFalse();
    }

    public List<Equipment> getEquipmentsByAddedBy(String email) {
        return equipmentRepository.findByAddedBy(email);
    }

    public Optional<Equipment> getEquipmentById(Long id) {
        return equipmentRepository.findById(id);
    }

    public Optional<Equipment> updateEquipment(Long id, Equipment updatedEquipment) {
        return equipmentRepository.findById(id).map(existingEquipment -> {
            existingEquipment.setName(updatedEquipment.getName());
            existingEquipment.setCategory(updatedEquipment.getCategory());
            existingEquipment.setDescription(updatedEquipment.getDescription());
            existingEquipment.setImageUrl(updatedEquipment.getImageUrl());
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
        List<Equipment> equipments = equipmentRepository.findByVilleCentreIgnoreCaseAndValidatedTrue(villeCentre);
        return equipments.stream()
                .filter(equip -> equip.getStatus() == null || "Disponible".equalsIgnoreCase(equip.getStatus()))
                .collect(Collectors.toList());
    }


    public Map<String, Object> getEquipmentHistory(Long equipmentId) {
        Map<String, Object> history = new HashMap<>();
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(equipmentId);

        if (equipmentOpt.isEmpty()) {
            return history;
        }

        Equipment equipment = equipmentOpt.get();
        history.put("equipmentId", equipment.getId());
        history.put("equipmentName", equipment.getName());
        history.put("villeCentre", equipment.getVilleCentre());

        // Implémentation basique - à adapter selon votre modèle de Demand
        List<Map<String, Object>> utilisations = new ArrayList<>();
        /*
        List<Demand> demands = demandRepository.findByEquipmentIdAndStatus(equipmentId, "VALIDEE");
        for (Demand demand : demands) {
            Map<String, Object> utilisation = new HashMap<>();
            Utilisateur user = demand.getUser();
            utilisation.put("nom", user.getNom());
            utilisation.put("prenom", user.getPrenom());
            utilisation.put("email", user.getEmail());
            utilisation.put("telephone", user.getPhone());
            utilisation.put("heuresUtilisation", demand.getDuree());
            utilisations.add(utilisation);
        }
        */

        history.put("utilisations", utilisations);
        history.put("totalUtilisations", utilisations.size());

        return history;
    }

    public List<String> getAllCenters() {
        return Arrays.stream(Utilisateur.VilleCentre.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}