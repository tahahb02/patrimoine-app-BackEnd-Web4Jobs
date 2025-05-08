package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.Equipment;
import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<Equipment>> getAllEquipments(@RequestHeader("Authorization") String token,
                                                            @RequestHeader("X-User-Role") String userRole) {
        if (!"RESPONSABLE_PATRIMOINE".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Equipment> equipments = equipmentService.getAllEquipments();
        return ResponseEntity.ok(equipments);
    }


    @GetMapping("/equipments/validated")
    public ResponseEntity<List<Equipment>> getValidatedEquipments() {
        List<Equipment> equipments = equipmentService.getValidatedEquipments();
        return ResponseEntity.ok(equipments);
    }

    @GetMapping("/historique-equipements/{id}")
    public ResponseEntity<Map<String, Object>> getEquipmentHistory(@PathVariable Long id) {
        Map<String, Object> history = equipmentService.getEquipmentHistory(id);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/centers")
    public ResponseEntity<List<String>> getAllCenters() {
        List<String> centers = equipmentService.getAllCenters();
        return ResponseEntity.ok(centers);
    }
}