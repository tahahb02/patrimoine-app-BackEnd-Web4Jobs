package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.DiagnosticEquipement;
import com.patrimoine.backend.service.DiagnosticEquipementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/diagnostics")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", maxAge = 3600)
public class DiagnosticEquipementController {

    @Autowired
    private DiagnosticEquipementService diagnosticService;

    @GetMapping("/ville/{villeCentre}")
    public ResponseEntity<List<DiagnosticEquipement>> getDiagnosticsByVilleCentre(
            @PathVariable String villeCentre,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Center") String userCenter) {

        if (!"TECHNICIEN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!villeCentre.equals(userCenter)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<DiagnosticEquipement> diagnostics = diagnosticService.getDiagnosticsByVilleCentre(villeCentre);
        return ResponseEntity.ok(diagnostics);
    }

    @GetMapping("/maintenance/ville/{villeCentre}")
    public ResponseEntity<List<DiagnosticEquipement>> getDiagnosticsBesoinMaintenance(
            @PathVariable String villeCentre,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Center") String userCenter) {

        if (!"TECHNICIEN".equals(userRole) && !"RESPONSABLE_PATRIMOINE".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!villeCentre.equals(userCenter)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<DiagnosticEquipement> diagnostics = diagnosticService.getDiagnosticsBesoinMaintenance(villeCentre);
        return ResponseEntity.ok(diagnostics);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDiagnostics(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        System.out.println("Requête GET /api/diagnostics/all reçue");
        System.out.println("Rôle utilisateur: " + userRole);

        // Temporairement désactivé pour le debug
        // if (!"DIRECTEUR".equals(userRole)) {
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès réservé aux directeurs");
        // }

        try {
            List<DiagnosticEquipement> diagnostics = diagnosticService.getAllDiagnostics();
            System.out.println("Nombre de diagnostics trouvés: " + diagnostics.size());
            return ResponseEntity.ok(diagnostics);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des diagnostics: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/evaluation")
    public ResponseEntity<DiagnosticEquipement> evaluerDiagnostic(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @RequestHeader("X-User-Role") String userRole) {

        if (!"TECHNICIEN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        boolean besoinMaintenance = Boolean.parseBoolean(request.get("besoinMaintenance"));
        String typeProbleme = request.get("typeProbleme");
        String degreUrgence = request.get("degreUrgence");
        String description = request.get("description");
        int dureeEstimee = Integer.parseInt(request.get("dureeEstimee"));

        DiagnosticEquipement updated = diagnosticService.updateDiagnostic(
                id, besoinMaintenance, typeProbleme, degreUrgence, description, dureeEstimee);

        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/maintenance-effectuee")
    public ResponseEntity<Void> marquerMaintenanceEffectuee(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String userRole) {

        if (!"TECHNICIEN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        diagnosticService.marquerMaintenanceEffectuee(id);
        return ResponseEntity.ok().build();
    }
}