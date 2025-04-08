package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.Equipment;
import com.patrimoine.backend.service.DemandeEquipementService;
import com.patrimoine.backend.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/historique-equipements")
@CrossOrigin(origins = "http://localhost:3000")
public class HistoriqueEquipementController {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private DemandeEquipementService demandeEquipementService;

    @GetMapping("/{equipementId}")
    public ResponseEntity<Map<String, Object>> getHistoriqueUtilisation(
            @PathVariable String equipementId) {
        Map<String, Object> statistiques =
                demandeEquipementService.getStatistiquesUtilisationEquipement(equipementId);

        if (statistiques.get("utilisations") == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(statistiques);
    }
}