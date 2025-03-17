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

    // ✅ Ajouter un équipement
    public Equipment addEquipment(Equipment equipment) {
        if (equipment.getDateAdded() == null) {
            // Si la date n'est pas fournie, la générer automatiquement au format String
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            equipment.setDateAdded(LocalDateTime.now().format(formatter)); // Format ISO pour la date
        }
        return equipmentRepository.save(equipment);
    }

    // ✅ Récupérer tous les équipements
    public List<Equipment> getAllEquipments() {
        return equipmentRepository.findAll();
    }

    // ✅ Récupérer un équipement par ID
    public Optional<Equipment> getEquipmentById(Long id) {
        return equipmentRepository.findById(id);
    }

    // ✅ Modifier un équipement
    public Optional<Equipment> updateEquipment(Long id, Equipment updatedEquipment) {
        return equipmentRepository.findById(id).map(existingEquipment -> {
            existingEquipment.setName(updatedEquipment.getName());
            existingEquipment.setCategory(updatedEquipment.getCategory());
            existingEquipment.setCenter(updatedEquipment.getCenter());
            return equipmentRepository.save(existingEquipment);
        });
    }

    // ✅ Supprimer un équipement
    public boolean deleteEquipment(Long id) {
        if (equipmentRepository.existsById(id)) {
            equipmentRepository.deleteById(id);
            return true; // Suppression réussie
        } else {
            return false; // Équipement non trouvé
        }
    }
}
