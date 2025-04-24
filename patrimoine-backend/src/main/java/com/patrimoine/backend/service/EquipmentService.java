package com.patrimoine.backend.service;

import com.patrimoine.backend.entity.Equipment;
import com.patrimoine.backend.repository.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public Equipment addEquipment(Equipment equipment) {
        if (equipment.getDateAdded() == null) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            equipment.setDateAdded(LocalDateTime.now().format(formatter));
        }
        return equipmentRepository.save(equipment);
    }

    public List<Equipment> getAllEquipments() {
        return equipmentRepository.findAll();
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

    public boolean deleteEquipment(Long id) {
        if (equipmentRepository.existsById(id)) {
            equipmentRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public List<Equipment> getEquipmentsByVilleCentre(String villeCentre) {
        return equipmentRepository.findByVilleCentreIgnoreCase(villeCentre);
    }
}