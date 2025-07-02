package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.repository.UtilisateurRepository;
import com.patrimoine.backend.repository.EquipmentRepository;
import com.patrimoine.backend.repository.DemandeEquipementRepository;
import com.patrimoine.backend.repository.DiagnosticEquipementRepository;
import com.patrimoine.backend.repository.MaintenanceEquipementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "http://localhost:3000")
public class StatisticsController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private DemandeEquipementRepository demandeEquipementRepository;

    @Autowired
    private DiagnosticEquipementRepository diagnosticEquipementRepository;

    @Autowired
    private MaintenanceEquipementRepository maintenanceEquipementRepository;

    @GetMapping("/directeur")
    public ResponseEntity<Map<String, Object>> getDirectorStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // User statistics
        stats.put("totalUsers", utilisateurRepository.count());
        stats.put("totalAdherants", utilisateurRepository.countByRole(Utilisateur.Role.ADHERANT));
        stats.put("totalResponsables", utilisateurRepository.countByRole(Utilisateur.Role.RESPONSABLE));
        stats.put("totalTechniciens", utilisateurRepository.countByRole(Utilisateur.Role.TECHNICIEN));
        stats.put("totalRP", utilisateurRepository.countByRole(Utilisateur.Role.RESPONSABLE_PATRIMOINE));

        // Equipment statistics
        stats.put("totalEquipments", equipmentRepository.count());
        stats.put("totalCenters", Utilisateur.VilleCentre.values().length);

        // Equipment status
        Map<String, Long> equipmentStatus = new HashMap<>();
        equipmentStatus.put("available", equipmentRepository.countByStatus("Disponible"));
        equipmentStatus.put("onLoan", demandeEquipementRepository.countByStatut("ACCEPTEE"));
        equipmentStatus.put("maintenance", equipmentRepository.countByEnMaintenance(true));
        equipmentStatus.put("diagnostic", diagnosticEquipementRepository.countByMaintenanceEffectuee(false));
        stats.put("equipmentStatus", equipmentStatus);

        // Maintenance statistics
        Map<String, Long> maintenanceStats = new HashMap<>();
        maintenanceStats.put("inProgress", maintenanceEquipementRepository.countByTermine(false));
        maintenanceStats.put("completed", maintenanceEquipementRepository.countByTermine(true));
        maintenanceStats.put("planned", diagnosticEquipementRepository.countByBesoinMaintenance(true));
        stats.put("maintenanceStats", maintenanceStats);

        // Diagnostic statistics
        Map<String, Long> diagnosticStats = new HashMap<>();
        diagnosticStats.put("pending", diagnosticEquipementRepository.countByMaintenanceEffectuee(false));
        diagnosticStats.put("completed", diagnosticEquipementRepository.countByMaintenanceEffectuee(true));
        stats.put("diagnosticStats", diagnosticStats);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/general")
    public ResponseEntity<Map<String, Object>> getGeneralStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Totaux par rôle
        stats.put("totalUsers", utilisateurRepository.count());
        stats.put("totalAdherants", utilisateurRepository.countByRole(Utilisateur.Role.ADHERANT));
        stats.put("totalResponsables", utilisateurRepository.countByRole(Utilisateur.Role.RESPONSABLE));
        stats.put("totalTechniciens", utilisateurRepository.countByRole(Utilisateur.Role.TECHNICIEN));
        stats.put("totalRP", utilisateurRepository.countByRole(Utilisateur.Role.RESPONSABLE_PATRIMOINE));

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/adherants/evolution")
    public ResponseEntity<Map<String, Object>> getAdherantEvolution(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LocalDate defaultStart = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate defaultEnd = LocalDate.now();

        List<Object[]> results = utilisateurRepository.countAdherantsByPeriod(
                startDate != null ? startDate : defaultStart,
                endDate != null ? endDate : defaultEnd
        );

        Map<String, Object> response = new HashMap<>();
        response.put("period", Map.of(
                "start", startDate != null ? startDate : defaultStart,
                "end", endDate != null ? endDate : defaultEnd
        ));
        response.put("data", results.stream()
                .map(r -> Map.of(
                        "date", r[0],
                        "count", r[1]
                ))
                .collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-center")
    public ResponseEntity<Map<String, Object>> getStatisticsByCenter() {
        Map<String, Object> stats = new HashMap<>();

        // Pour chaque centre, compter les utilisateurs par rôle
        for (Utilisateur.VilleCentre centre : Utilisateur.VilleCentre.values()) {
            Map<String, Long> centreStats = new HashMap<>();
            centreStats.put("total", utilisateurRepository.countByVilleCentre(centre));
            centreStats.put("adherants", utilisateurRepository.countByRoleAndVilleCentre(Utilisateur.Role.ADHERANT, centre));
            centreStats.put("responsables", utilisateurRepository.countByRoleAndVilleCentre(Utilisateur.Role.RESPONSABLE, centre));
            centreStats.put("techniciens", utilisateurRepository.countByRoleAndVilleCentre(Utilisateur.Role.TECHNICIEN, centre));
            centreStats.put("rp", utilisateurRepository.countByRoleAndVilleCentre(Utilisateur.Role.RESPONSABLE_PATRIMOINE, centre));

            stats.put(centre.name(), centreStats);
        }

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/new-users")
    public ResponseEntity<Map<String, Object>> getNewUsersStatistics(
            @RequestParam(defaultValue = "week") String period) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;

        switch (period.toLowerCase()) {
            case "day":
                startDate = now.minusDays(1);
                break;
            case "week":
                startDate = now.minusWeeks(1);
                break;
            case "month":
                startDate = now.minusMonths(1);
                break;
            case "year":
                startDate = now.minusYears(1);
                break;
            default:
                startDate = now.minusWeeks(1);
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("period", period);
        stats.put("totalNewUsers", utilisateurRepository.countByCreatedAtAfter(startDate));
        stats.put("newAdherants", utilisateurRepository.countByRoleAndCreatedAtAfter(Utilisateur.Role.ADHERANT, startDate));
        stats.put("newResponsables", utilisateurRepository.countByRoleAndCreatedAtAfter(Utilisateur.Role.RESPONSABLE, startDate));
        stats.put("newTechniciens", utilisateurRepository.countByRoleAndCreatedAtAfter(Utilisateur.Role.TECHNICIEN, startDate));
        stats.put("newRP", utilisateurRepository.countByRoleAndCreatedAtAfter(Utilisateur.Role.RESPONSABLE_PATRIMOINE, startDate));

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/equipments/status")
    public ResponseEntity<Map<String, Object>> getEquipmentStatusStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalEquipments", equipmentRepository.count());
        stats.put("validatedEquipments", equipmentRepository.countByValidatedTrue());
        stats.put("pendingValidation", equipmentRepository.countByValidatedFalse());
        stats.put("inMaintenance", equipmentRepository.countByEnMaintenance(true));

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/demandes/status")
    public ResponseEntity<Map<String, Object>> getDemandeStatusStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalDemandes", demandeEquipementRepository.count());
        stats.put("pending", demandeEquipementRepository.countByStatut("EN_ATTENTE"));
        stats.put("accepted", demandeEquipementRepository.countByStatut("ACCEPTEE"));
        stats.put("rejected", demandeEquipementRepository.countByStatut("REFUSEE"));
        stats.put("delivered", demandeEquipementRepository.countByStatut("LIVREE"));
        stats.put("returned", demandeEquipementRepository.countByStatut("RETOURNEE"));

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/patrimoine")
    public ResponseEntity<Map<String, Object>> getPatrimoineStatistics(
            @RequestHeader("X-User-Role") String userRole) {

        // Debug log
        System.out.println("Accessing /patrimoine with role: " + userRole);

        if (!"RESPONSABLE_PATRIMOINE".equals(userRole)) {
            System.out.println("Access denied for role: " + userRole);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Map<String, Object> stats = new HashMap<>();

            // Debug logs
            System.out.println("Counting equipment...");
            long totalEquipments = equipmentRepository.count();
            System.out.println("Total equipments: " + totalEquipments);

            stats.put("totalEquipments", totalEquipments);
            stats.put("validatedEquipments", equipmentRepository.countByValidatedTrue());
            stats.put("pendingValidation", equipmentRepository.countByValidatedFalse());
            stats.put("centersCount", Utilisateur.VilleCentre.values().length);
            stats.put("totalUsers", utilisateurRepository.count());
            stats.put("totalRP", utilisateurRepository.countByRole(Utilisateur.Role.RESPONSABLE_PATRIMOINE));

            // Equipment status
            Map<String, Long> equipmentStatus = new HashMap<>();
            equipmentStatus.put("available", equipmentRepository.countByStatus("Disponible"));
            equipmentStatus.put("onLoan", demandeEquipementRepository.countByStatut("ACCEPTEE"));
            equipmentStatus.put("maintenance", equipmentRepository.countByEnMaintenance(true));
            stats.put("equipmentStatus", equipmentStatus);

            // Maintenance stats
            Map<String, Long> maintenanceStats = new HashMap<>();
            maintenanceStats.put("inProgress", maintenanceEquipementRepository.countByTermine(false));
            maintenanceStats.put("completed", maintenanceEquipementRepository.countByTermine(true));
            maintenanceStats.put("planned", diagnosticEquipementRepository.countByBesoinMaintenance(true));
            stats.put("maintenanceStats", maintenanceStats);

            System.out.println("Statistics prepared: " + stats);
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            System.err.println("Error in getPatrimoineStatistics: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}