package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.Equipment;
import com.patrimoine.backend.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rp")
@CrossOrigin(origins = "http://localhost:3000")
public class ResponsablePatrimoineController {

    @Autowired
    private EquipmentService equipmentService;

    // Récupérer tous les équipements tous centres confondus
    @GetMapping("/equipments")
    public ResponseEntity<List<Equipment>> getAllEquipments() {
        List<Equipment> equipments = equipmentService.getAllEquipments();
        return ResponseEntity.ok(equipments);
    }

    // Ajouter un équipement avec spécification du centre
    @PostMapping("/equipments/add")
    public ResponseEntity<Equipment> addEquipment(@RequestBody Equipment equipment) {
        if (equipment.getVilleCentre() == null || equipment.getVilleCentre().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Equipment savedEquipment = equipmentService.addEquipment(equipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEquipment);
    }

    // Valider un équipement ajouté par un responsable
    @PutMapping("/equipments/validate/{id}")
    public ResponseEntity<Equipment> validateEquipment(@PathVariable Long id) {
        Optional<Equipment> updated = equipmentService.validateEquipment(id);
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/equipments/delete/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        boolean deleted = equipmentService.deleteEquipment(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/equipments/update/{id}")
    public ResponseEntity<Equipment> updateEquipment(@PathVariable Long id, @RequestBody Equipment equipment) {
        Optional<Equipment> existing = equipmentService.getEquipmentById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Conserver les valeurs existantes si non fournies
        Equipment existingEquipment = existing.get();
        if (equipment.getName() == null) equipment.setName(existingEquipment.getName());
        if (equipment.getCategory() == null) equipment.setCategory(existingEquipment.getCategory());
        if (equipment.getVilleCentre() == null) equipment.setVilleCentre(existingEquipment.getVilleCentre());
        if (equipment.getDescription() == null) equipment.setDescription(existingEquipment.getDescription());
        if (equipment.getImageUrl() == null) equipment.setImageUrl(existingEquipment.getImageUrl());

        Optional<Equipment> updatedEquipment = equipmentService.updateEquipment(id, equipment);
        return updatedEquipment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}