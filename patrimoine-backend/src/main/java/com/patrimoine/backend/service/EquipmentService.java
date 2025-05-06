package com.patrimoine.backend.service;

import com.patrimoine.backend.entity.Equipment;
import com.patrimoine.backend.repository.EquipmentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
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
}