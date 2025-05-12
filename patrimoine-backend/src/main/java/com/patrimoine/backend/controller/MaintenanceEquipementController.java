
package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.MaintenanceEquipement;
import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.service.MaintenanceEquipementService;
import com.patrimoine.backend.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenances")
@CrossOrigin(origins = "http://localhost:3000")
public class MaintenanceEquipementController {

    @Autowired
    private MaintenanceEquipementService maintenanceService;

    @Autowired
    private UtilisateurService utilisateurService;

    @PostMapping
    public ResponseEntity<MaintenanceEquipement> creerMaintenance(
            @RequestBody Map<String, String> request,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Id") Long userId) {

        if (!"TECHNICIEN".equals(userRole) && !"RESPONSABLE_PATRIMOINE".equals(userRole)) {
            return ResponseEntity.status(403).build();
        }

        Utilisateur technicien = utilisateurService.getUtilisateurById(userId);
        if (technicien == null) {
            return ResponseEntity.badRequest().build();
        }

        MaintenanceEquipement maintenance = maintenanceService.creerMaintenance(
                request.get("idEquipement"),
                request.get("nomEquipement"),
                request.get("categorie"),
                request.get("villeCentre"),
                request.get("typeProbleme"),
                request.get("description"),
                Integer.parseInt(request.get("dureeEstimee")),
                technicien);

        return ResponseEntity.ok(maintenance);
    }

    @PutMapping("/{id}/terminer")
    public ResponseEntity<MaintenanceEquipement> terminerMaintenance(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @RequestHeader("X-User-Role") String userRole) {

        if (!"TECHNICIEN".equals(userRole)) {
            return ResponseEntity.status(403).build();
        }

        MaintenanceEquipement updated = maintenanceService.terminerMaintenance(
                id,
                request.get("actionsRealisees"),
                Integer.parseInt(request.get("dureeReelle")));

        return ResponseEntity.ok(updated);
    }

    @GetMapping("/ville/{villeCentre}")
    public ResponseEntity<List<MaintenanceEquipement>> getMaintenancesByVilleCentre(
            @PathVariable String villeCentre,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Center") String userCenter) {

        if (!"TECHNICIEN".equals(userRole) && !"RESPONSABLE_PATRIMOINE".equals(userRole)) {
            return ResponseEntity.status(403).build();
        }

        if (!villeCentre.equals(userCenter)) {
            return ResponseEntity.status(403).build();
        }

        List<MaintenanceEquipement> maintenances = maintenanceService.getMaintenancesByVilleCentre(villeCentre);
        return ResponseEntity.ok(maintenances);
    }

    @GetMapping("/en-cours/ville/{villeCentre}")
    public ResponseEntity<List<MaintenanceEquipement>> getMaintenancesEnCours(
            @PathVariable String villeCentre,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Center") String userCenter) {

        if (!"TECHNICIEN".equals(userRole) && !"RESPONSABLE_PATRIMOINE".equals(userRole)) {
            return ResponseEntity.status(403).build();
        }

        if (!villeCentre.equals(userCenter)) {
            return ResponseEntity.status(403).build();
        }

        List<MaintenanceEquipement> maintenances = maintenanceService.getMaintenancesEnCours(villeCentre);
        return ResponseEntity.ok(maintenances);
    }

    @GetMapping("/historique/ville/{villeCentre}")
    public ResponseEntity<List<MaintenanceEquipement>> getHistoriqueMaintenances(
            @PathVariable String villeCentre,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Center") String userCenter) {

        if (!"TECHNICIEN".equals(userRole) && !"RESPONSABLE_PATRIMOINE".equals(userRole)) {
            return ResponseEntity.status(403).build();
        }

        if (!villeCentre.equals(userCenter)) {
            return ResponseEntity.status(403).build();
        }

        List<MaintenanceEquipement> maintenances = maintenanceService.getHistoriqueMaintenances(villeCentre);
        return ResponseEntity.ok(maintenances);
    }

    @GetMapping("/technicien/{technicienId}")
    public ResponseEntity<List<MaintenanceEquipement>> getMaintenancesByTechnicien(
            @PathVariable Long technicienId,
            @RequestHeader("X-User-Role") String userRole) {

        if (!"TECHNICIEN".equals(userRole) && !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(403).build();
        }

        List<MaintenanceEquipement> maintenances = maintenanceService.getMaintenancesByTechnicien(technicienId);
        return ResponseEntity.ok(maintenances);
    }
}