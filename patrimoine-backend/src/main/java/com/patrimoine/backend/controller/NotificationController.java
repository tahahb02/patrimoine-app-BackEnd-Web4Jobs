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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationRepository.findByUtilisateurIdOrderByDateCreationDesc(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationRepository.findByUtilisateurIdAndLueFalseOrderByDateCreationDesc(userId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/marquer-lue")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        return notificationRepository.findById(id)
                .map(notification -> {
                    notification.setLue(true);
                    notificationRepository.save(notification);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/marquer-lue-par-lien")
    public ResponseEntity<?> markAsReadByLink(@RequestBody Map<String, String> request) {
        String link = request.get("link");
        if (link == null) {
            return ResponseEntity.badRequest().body("Lien manquant");
        }

        return notificationRepository.findByLink(link)
                .map(notification -> {
                    notification.setLue(true);
                    notificationRepository.save(notification);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/user/{userId}/marquer-toutes-lues")
    public ResponseEntity<?> markAllAsRead(@PathVariable Long userId) {
        notificationRepository.markAllAsReadByUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/feedback/{equipmentId}")
    public ResponseEntity<List<Notification>> getFeedbackNotificationsForEquipment(@PathVariable String equipmentId) {
        List<Notification> notifications = notificationRepository.findFeedbackNotificationsForEquipment(equipmentId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/centre/{villeCentre}")
    public ResponseEntity<List<Notification>> getNotificationsByCentre(@PathVariable String villeCentre) {
        List<Notification> notifications = notificationRepository.findByVilleCentre(villeCentre);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/count/user/{userId}/unread")
    public ResponseEntity<Long> countUnreadNotifications(@PathVariable Long userId) {
        long count = notificationRepository.countUnreadByUser(userId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/feedback")
    public ResponseEntity<?> createFeedbackNotification(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            String equipmentId = request.get("equipmentId").toString();
            String equipmentName = request.get("equipmentName").toString();
            String villeCentre = request.get("villeCentre").toString();
            Long relatedId = Long.parseLong(request.get("relatedId").toString());

            // Trouver les utilisateurs à notifier (responsables et techniciens du centre)
            List<Utilisateur> usersToNotify = utilisateurRepository.findByRoleAndVilleCentreOrRole(
                    Utilisateur.Role.RESPONSABLE,
                    Utilisateur.VilleCentre.valueOf(villeCentre.replace(" ", "_").toUpperCase()),
                    Utilisateur.Role.RESPONSABLE_PATRIMOINE
            );

            // Créer les notifications
            usersToNotify.forEach(user -> {
                Notification notification = new Notification();
                notification.setUtilisateur(user);
                notification.setType("FEEDBACK");
                notification.setTitre("Nouveau feedback reçu");
                notification.setMessage(String.format(
                        "Nouveau feedback sur l'équipement %s (%s) dans le centre %s",
                        equipmentName, equipmentId, villeCentre
                ));
                notification.setEquipmentId(equipmentId);
                notification.setEquipmentName(equipmentName);
                notification.setRelatedId(relatedId);
                notification.setLink("/feedback/" + relatedId);
                notificationRepository.save(notification);
            });

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la création des notifications: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}