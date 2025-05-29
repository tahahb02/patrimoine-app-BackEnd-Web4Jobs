package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.*;
import com.patrimoine.backend.repository.*;
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

    @Autowired
    private DemandeEquipementRepository demandeEquipementRepository;

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
            Long demandeId = Long.parseLong(request.get("demandeId").toString());

            Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
            Optional<DemandeEquipement> demandeOpt = demandeEquipementRepository.findById(demandeId);

            if (userOpt.isEmpty() || demandeOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Utilisateur ou demande non trouvé");
            }

            DemandeEquipement demande = demandeOpt.get();

            Notification notification = new Notification();
            notification.setUtilisateur(userOpt.get());
            notification.setType("FEEDBACK");
            notification.setTitre("Feedback requis");
            notification.setMessage(String.format(
                    "Merci d'évaluer votre utilisation de %s (ID: %s)",
                    demande.getNomEquipement(), demande.getIdEquipement()
            ));
            notification.setDateCreation(LocalDateTime.now());
            notification.setLue(false);
            notification.setLink("/FormulaireFeedback/" + demande.getId());
            notification.setEquipmentId(demande.getIdEquipement());
            notification.setEquipmentName(demande.getNomEquipement());
            notification.setRelatedId(demande.getId());

            notificationRepository.save(notification);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    @PostMapping("/create-feedback-notif")
    public ResponseEntity<?> createFeedbackNotificationSimple(@RequestBody Map<String, Long> request) {
        try {
            Long userId = request.get("userId");
            Long demandeId = request.get("demandeId");

            Utilisateur user = utilisateurRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            DemandeEquipement demande = demandeEquipementRepository.findById(demandeId)
                    .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

            Notification notification = new Notification();
            notification.setUtilisateur(user);
            notification.setType("FEEDBACK");
            notification.setTitre("Feedback requis");
            notification.setMessage(String.format(
                    "Merci d'évaluer l'équipement %s (ID: %s) que vous avez utilisé",
                    demande.getNomEquipement(), demande.getIdEquipement()
            ));
            notification.setDateCreation(LocalDateTime.now());
            notification.setLue(false);
            notification.setLink("/FormulaireFeedback/" + demande.getId());
            notification.setEquipmentId(demande.getIdEquipement());
            notification.setEquipmentName(demande.getNomEquipement());

            notificationRepository.save(notification);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
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