package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.Notification;
import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.repository.NotificationRepository;
import com.patrimoine.backend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @PostMapping("/feedback")
    public ResponseEntity<?> createFeedbackNotification(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            String equipmentId = request.get("equipmentId").toString();
            String equipmentName = request.get("equipmentName").toString();
            String dateUtilisation = request.get("dateUtilisation").toString();
            Long demandeId = Long.parseLong(request.get("demandeId").toString());

            Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Utilisateur non trouvé");
            }

            Notification notification = new Notification();
            notification.setUtilisateur(userOpt.get());
            notification.setType("FEEDBACK");
            notification.setTitre("Évaluation de l'équipement");
            notification.setMessage(String.format(
                    "Veuillez évaluer votre utilisation de %s (ID: %s) du %s",
                    equipmentName, equipmentId, dateUtilisation
            ));
            notification.setDateCreation(LocalDateTime.now());
            notification.setLue(false);
            notification.setLink("/FormulaireFeedback/" + demandeId);
            notification.setEquipmentId(equipmentId);
            notification.setEquipmentName(equipmentName);

            notificationRepository.save(notification);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la création de la notification");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationRepository.findByUtilisateurIdOrderByDateCreationDesc(userId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/marquer-lue")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        return notificationRepository.findById(id).map(notification -> {
            notification.setLue(true);
            notificationRepository.save(notification);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}