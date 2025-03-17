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
@CrossOrigin(origins = "http://localhost:3000") // Autoriser React
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    // ✅ Ajouter un équipement
    @PostMapping("/add")
    public ResponseEntity<Equipment> addEquipment(@RequestBody Equipment equipment) {
        if (equipment.getName() == null || equipment.getCategory() == null || equipment.getCenter() == null) {
            return ResponseEntity.badRequest().build();
        }

        Equipment savedEquipment = equipmentService.addEquipment(equipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEquipment);
    }

    // ✅ Obtenir tous les équipements
    @GetMapping
    public ResponseEntity<List<Equipment>> getAllEquipments() {
        System.out.println("🚀 Requête GET /api/equipments reçue"); // Vérification dans la console
        List<Equipment> equipments = equipmentService.getAllEquipments();
        return ResponseEntity.ok(equipments);
    }

    // ✅ Obtenir un équipement par ID
    @GetMapping("/{id}")
    public ResponseEntity<Equipment> getEquipmentById(@PathVariable Long id) {
        Optional<Equipment> equipment = equipmentService.getEquipmentById(id);
        return equipment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // ✅ Modifier un équipement
    @PutMapping("/update/{id}")
    public ResponseEntity<Equipment> updateEquipment(@PathVariable Long id, @RequestBody Equipment equipment) {
        if (equipment.getName() == null || equipment.getCategory() == null || equipment.getCenter() == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Equipment> updatedEquipment = equipmentService.updateEquipment(id, equipment);
        return updatedEquipment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // ✅ Supprimer un équipement
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        boolean deleted = equipmentService.deleteEquipment(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
