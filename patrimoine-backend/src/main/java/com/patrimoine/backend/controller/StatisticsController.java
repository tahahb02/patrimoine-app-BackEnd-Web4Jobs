package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

    @GetMapping("/general")
    public ResponseEntity<Map<String, Object>> getGeneralStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Totaux par rôle
        stats.put("totalUsers", utilisateurRepository.count());
        stats.put("totalAdherants", utilisateurRepository.countByRole(Utilisateur.Role.ADHERANT));
        stats.put("totalResponsables", utilisateurRepository.countByRole(Utilisateur.Role.RESPONSABLE));
        stats.put("totalTechniciens", utilisateurRepository.countByRole(Utilisateur.Role.TECHNICIEN));

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

        return ResponseEntity.ok(stats);
    }
}