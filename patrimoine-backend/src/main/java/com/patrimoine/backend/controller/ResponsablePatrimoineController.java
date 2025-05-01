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

    // Autres endpoints nécessaires...
}