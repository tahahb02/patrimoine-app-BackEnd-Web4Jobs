package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.Equipment;
import com.patrimoine.backend.service.EquipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/equipments")
@CrossOrigin(origins = "http://localhost:3000")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping("/add")
    public ResponseEntity<Equipment> addEquipment(
            @RequestBody Equipment equipment,
            @RequestHeader("X-User-Center") String userCenter) {

        // Fixer automatiquement la ville du centre
        equipment.setVilleCentre(userCenter);

        if (equipment.getName() == null || equipment.getCategory() == null) {
            return ResponseEntity.badRequest().build();
        }

        Equipment savedEquipment = equipmentService.addEquipment(equipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEquipment);
    }

    @GetMapping
    public ResponseEntity<List<Equipment>> getAllEquipments(
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Center") String userCenter) {

        List<Equipment> equipments;
        if ("ADMIN".equals(userRole)) {
            equipments = equipmentService.getAllEquipments();
        } else {
            equipments = equipmentService.getEquipmentsByVilleCentre(userCenter);
        }
        return ResponseEntity.ok(equipments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipment> getEquipmentById(@PathVariable Long id) {
        Optional<Equipment> equipment = equipmentService.getEquipmentById(id);
        return equipment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Equipment> updateEquipment(
            @PathVariable Long id,
            @RequestBody Equipment equipment,
            @RequestHeader("X-User-Center") String userCenter) {

        // Pour la modification, on conserve la ville d'origine
        Optional<Equipment> existing = equipmentService.getEquipmentById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        equipment.setVilleCentre(existing.get().getVilleCentre());

        if (equipment.getName() == null || equipment.getCategory() == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Equipment> updatedEquipment = equipmentService.updateEquipment(id, equipment);
        return updatedEquipment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        boolean deleted = equipmentService.deleteEquipment(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}