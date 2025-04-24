package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.Equipment;
import com.patrimoine.backend.service.EquipmentService;
import com.patrimoine.backend.service.DemandeEquipementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/equipments")
@CrossOrigin(origins = "http://localhost:3000")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final DemandeEquipementService demandeEquipementService;

    public EquipmentController(EquipmentService equipmentService,
                               DemandeEquipementService demandeEquipementService) {
        this.equipmentService = equipmentService;
        this.demandeEquipementService = demandeEquipementService;
    }

    @PostMapping("/add")
    public ResponseEntity<Equipment> addEquipment(@RequestBody Equipment equipment) {
        if (equipment.getName() == null || equipment.getCategory() == null || equipment.getVilleCentre() == null) {
            return ResponseEntity.badRequest().build();
        }
        Equipment savedEquipment = equipmentService.addEquipment(equipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEquipment);
    }

    @GetMapping
    public ResponseEntity<List<Equipment>> getAllEquipments() {
        List<Equipment> equipments = equipmentService.getAllEquipments();
        return ResponseEntity.ok(equipments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipment> getEquipmentById(@PathVariable Long id) {
        Optional<Equipment> equipment = equipmentService.getEquipmentById(id);
        return equipment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Equipment> updateEquipment(@PathVariable Long id, @RequestBody Equipment equipment) {
        if (equipment.getName() == null || equipment.getCategory() == null || equipment.getVilleCentre() == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Equipment> updatedEquipment = equipmentService.updateEquipment(id, equipment);
        return updatedEquipment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        boolean deleted = equipmentService.deleteEquipment(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}/historique")
    public ResponseEntity<Map<String, Object>> getHistoriqueEquipement(@PathVariable Long id) {
        Optional<Equipment> equipment = equipmentService.getEquipmentById(id);
        if (!equipment.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> historique =
                demandeEquipementService.getStatistiquesUtilisationEquipement(equipment.get().getId().toString());
        return ResponseEntity.ok(historique);
    }

    // 🔍 Nouvelle route pour filtrer selon la ville du centre
    @GetMapping("/ville/{villeCentre}")
    public ResponseEntity<List<Equipment>> getEquipmentsByVilleCentre(@PathVariable String villeCentre) {
        List<Equipment> equipments = equipmentService.getEquipmentsByVilleCentre(villeCentre);
        return ResponseEntity.ok(equipments);
    }
}
