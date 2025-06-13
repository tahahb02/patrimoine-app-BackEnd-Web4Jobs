package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.*;
import com.patrimoine.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        // Récupérer le rôle de l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(userId).orElse(null);
        if (utilisateur == null) {
            return ResponseEntity.notFound().build();
        }

        List<Notification> notifications;
        if (utilisateur.getRole() == Utilisateur.Role.ADHERANT) {
            // Adhérent voit seulement les feedbacks et réponses
            notifications = notificationRepository.findByUtilisateurIdAndTypeInOrderByDateCreationDesc(
                    userId,
                    List.of("FEEDBACK", "REPONSE")
            );
        } else if (utilisateur.getRole() == Utilisateur.Role.RESPONSABLE) {
            // Responsable voit seulement les demandes
            notifications = notificationRepository.findByUtilisateurIdAndTypeOrderByDateCreationDesc(
                    userId,
                    "DEMANDE"
            );
        } else {
            // Autres rôles voient tout
            notifications = notificationRepository.findByUtilisateurIdOrderByDateCreationDesc(userId);
        }

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadUserNotifications(@PathVariable Long userId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId).orElse(null);
        if (utilisateur == null) {
            return ResponseEntity.notFound().build();
        }

        List<Notification> notifications;
        if (utilisateur.getRole() == Utilisateur.Role.ADHERANT) {
            notifications = notificationRepository.findByUtilisateurIdAndLueFalseAndTypeInOrderByDateCreationDesc(
                    userId,
                    List.of("FEEDBACK", "REPONSE")
            );
        } else if (utilisateur.getRole() == Utilisateur.Role.RESPONSABLE) {
            notifications = notificationRepository.findByUtilisateurIdAndLueFalseAndTypeOrderByDateCreationDesc(
                    userId,
                    "DEMANDE"
            );
        } else {
            notifications = notificationRepository.findByUtilisateurIdAndLueFalseOrderByDateCreationDesc(userId);
        }

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

    @GetMapping("/count/user/{userId}/unread")
    public ResponseEntity<Long> countUnreadNotifications(@PathVariable Long userId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId).orElse(null);
        if (utilisateur == null) {
            return ResponseEntity.notFound().build();
        }

        long count;
        if (utilisateur.getRole() == Utilisateur.Role.ADHERANT) {
            count = notificationRepository.countUnreadByUserAndTypeIn(
                    userId,
                    List.of("FEEDBACK", "REPONSE")
            );
        } else if (utilisateur.getRole() == Utilisateur.Role.RESPONSABLE) {
            count = notificationRepository.countUnreadByUserAndType(
                    userId,
                    "DEMANDE"
            );
        } else {
            count = notificationRepository.countUnreadByUser(userId);
        }

        return ResponseEntity.ok(count);
    }

    @PostMapping("/create-demande-notification")
    public ResponseEntity<?> createDemandeNotification(@RequestBody Map<String, Long> request) {
        try {
            Long demandeId = request.get("demandeId");
            DemandeEquipement demande = demandeEquipementRepository.findById(demandeId)
                    .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

            // Trouver les responsables du centre concerné
            List<Utilisateur> responsables = utilisateurRepository.findByRoleAndVilleCentre(
                    Utilisateur.Role.RESPONSABLE,
                    demande.getUtilisateur().getVilleCentre()
            );

            // Créer une notification pour chaque responsable
            for (Utilisateur responsable : responsables) {
                Notification notification = new Notification();
                notification.setUtilisateur(responsable);
                notification.setDemande(demande);
                notification.setType("DEMANDE");
                notification.setTitre("Nouvelle demande d'équipement");
                notification.setMessage(String.format(
                        "Nouvelle demande pour l'équipement %s par %s %s",
                        demande.getNomEquipement(),
                        demande.getUtilisateur().getPrenom(),
                        demande.getUtilisateur().getNom()
                ));
                notification.setDateCreation(LocalDateTime.now());
                notification.setLue(false);
                notification.setLink("/demandes/" + demande.getId());
                notification.setEquipmentId(demande.getIdEquipement());
                notification.setEquipmentName(demande.getNomEquipement());
                notification.setRelatedId(demande.getId());
                notification.setDemandeurNom(demande.getUtilisateur().getNom());
                notification.setDemandeurPrenom(demande.getUtilisateur().getPrenom());
                notification.setStatutDemande("EN_ATTENTE");

                notificationRepository.save(notification);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    @PostMapping("/create-reponse-notification")
    public ResponseEntity<?> createReponseNotification(@RequestBody Map<String, Object> request) {
        try {
            Long demandeId = Long.parseLong(request.get("demandeId").toString());
            String statut = request.get("statut").toString();
            String commentaire = request.get("commentaire").toString();

            DemandeEquipement demande = demandeEquipementRepository.findById(demandeId)
                    .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

            // Créer une notification pour l'adhérent
            Notification notification = new Notification();
            notification.setUtilisateur(demande.getUtilisateur());
            notification.setDemande(demande);
            notification.setType("REPONSE");
            notification.setTitre("Réponse à votre demande");
            notification.setMessage(String.format(
                    "Votre demande pour %s a été %s. %s",
                    demande.getNomEquipement(),
                    statut.equals("ACCEPTEE") ? "acceptée" : "refusée",
                    commentaire != null && !commentaire.isEmpty() ? "Commentaire: " + commentaire : ""
            ));
            notification.setDateCreation(LocalDateTime.now());
            notification.setLue(false);
            notification.setLink("/demandes/" + demande.getId());
            notification.setEquipmentId(demande.getIdEquipement());
            notification.setEquipmentName(demande.getNomEquipement());
            notification.setRelatedId(demande.getId());
            notification.setStatutDemande(statut);

            notificationRepository.save(notification);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
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
            notification.setDemande(demande);
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
            notification.setDemande(demande);
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