package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.Equipment;
import com.patrimoine.backend.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rp")
@CrossOrigin(origins = "http://localhost:3000")
public class ResponsablePatrimoineController {

    @Autowired
    private EquipmentService equipmentService;

    @GetMapping("/equipments/pending")
    public ResponseEntity<List<Equipment>> getPendingEquipments() {
        List<Equipment> equipments = equipmentService.getPendingEquipments();
        return ResponseEntity.ok(equipments);
    }

    @PutMapping("/equipments/validate/{id}")
    public ResponseEntity<Equipment> validateEquipment(@PathVariable Long id) {
        return equipmentService.validateEquipment(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/equipments/all")
    public ResponseEntity<List<Equipment>> getAllEquipments() {
        List<Equipment> equipments = equipmentService.getAllEquipments();
        return ResponseEntity.ok(equipments);
    }

    @GetMapping("/equipments/validated")
    public ResponseEntity<List<Equipment>> getValidatedEquipments() {
        List<Equipment> equipments = equipmentService.getValidatedEquipments();
        return ResponseEntity.ok(equipments);
    }


}