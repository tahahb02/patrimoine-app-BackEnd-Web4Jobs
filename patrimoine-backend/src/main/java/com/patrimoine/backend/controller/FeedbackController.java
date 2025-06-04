package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.*;
import com.patrimoine.backend.repository.*;
import com.patrimoine.backend.service.FeedbackAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "http://localhost:3000")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private DemandeEquipementRepository demandeEquipementRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private FeedbackAnalysisService feedbackAnalysisService;

    @PostMapping
    public ResponseEntity<?> createFeedback(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            Long demandeId = Long.parseLong(request.get("demandeId").toString());

            Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
            Optional<DemandeEquipement> demandeOpt = demandeEquipementRepository.findById(demandeId);

            if (userOpt.isEmpty() || demandeOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Utilisateur ou demande non trouvé");
            }

            Feedback feedback = new Feedback();
            feedback.setUtilisateur(userOpt.get());
            feedback.setDemande(demandeOpt.get());
            feedback.setEquipmentId(request.get("equipmentId").toString());
            feedback.setEquipmentName(request.get("equipmentName").toString());
            feedback.setSatisfaction(Integer.parseInt(request.get("satisfaction").toString()));
            feedback.setPerformance(Integer.parseInt(request.get("performance").toString()));
            feedback.setFaciliteUtilisation(Integer.parseInt(request.get("faciliteUtilisation").toString()));
            feedback.setFiabilite(Integer.parseInt(request.get("fiabilite").toString()));
            feedback.setCommentaires(request.get("commentaires").toString());
            feedback.setProblemesRencontres(request.get("problemesRencontres").toString());
            feedback.setProblemesTechniques((List<String>) request.get("problemesTechniques"));
            feedback.setRecommander(request.get("recommander").toString());
            feedback.setEmail(request.get("email").toString());
            feedback.setVilleCentre(demandeOpt.get().getVilleCentre());
            feedback.setDateFeedback(LocalDateTime.now());

            createNotificationsForFeedback(feedback);
            updateEquipmentBasedOnFeedback(feedback);

            Feedback savedFeedback = feedbackRepository.save(feedback);
            feedbackAnalysisService.analyzeFeedback(savedFeedback);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la création du feedback: " + e.getMessage());
        }
    }

    private void createNotificationsForFeedback(Feedback feedback) {
        List<Utilisateur> responsables = utilisateurRepository.findByRoleAndVilleCentre(
                Utilisateur.Role.RESPONSABLE,
                Utilisateur.VilleCentre.valueOf(feedback.getVilleCentre().replace(" ", "_").toUpperCase())
        );

        List<Utilisateur> responsablesPatrimoine = utilisateurRepository.findByRole(
                Utilisateur.Role.RESPONSABLE_PATRIMOINE
        );

        List<Utilisateur> techniciens = utilisateurRepository.findByRoleAndVilleCentre(
                Utilisateur.Role.TECHNICIEN,
                Utilisateur.VilleCentre.valueOf(feedback.getVilleCentre().replace(" ", "_").toUpperCase())
        );

        createNotificationForUsers(responsables, feedback, "Nouveau feedback reçu pour votre centre");
        createNotificationForUsers(responsablesPatrimoine, feedback, "Nouveau feedback reçu");
        createNotificationForUsers(techniciens, feedback, "Nouveau feedback technique reçu");
    }

    private void createNotificationForUsers(List<Utilisateur> users, Feedback feedback, String titre) {
        users.forEach(user -> {
            Notification notification = new Notification();
            notification.setUtilisateur(user);
            notification.setType("FEEDBACK");
            notification.setTitre(titre);
            notification.setMessage(String.format(
                    "Feedback sur l'équipement %s (%s) - Satisfaction: %d/5",
                    feedback.getEquipmentName(),
                    feedback.getEquipmentId(),
                    feedback.getSatisfaction()
            ));
            notification.setDateCreation(LocalDateTime.now());
            notification.setLue(false);
            notification.setLink("/feedback/" + feedback.getId());
            notificationRepository.save(notification);
        });
    }

    private void updateEquipmentBasedOnFeedback(Feedback feedback) {
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(Long.parseLong(feedback.getEquipmentId()));
        equipmentOpt.ifPresent(equipment -> {
            if (feedback.getSatisfaction() < 3 || feedback.getFiabilite() < 3) {
                equipment.setStatus("BESOIN_VERIFICATION");
                equipmentRepository.save(equipment);
            }
        });
    }

    @GetMapping("/centre/{villeCentre}")
    public ResponseEntity<List<Feedback>> getFeedbackByCentre(@PathVariable String villeCentre) {
        List<Feedback> feedbacks = feedbackRepository.findByVilleCentreOrderByDateFeedbackDesc(villeCentre);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<List<Feedback>> getFeedbackByEquipment(@PathVariable String equipmentId) {
        List<Feedback> feedbacks = feedbackRepository.findByEquipmentId(equipmentId);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        List<Feedback> feedbacks = feedbackRepository.findAllByOrderByDateFeedbackDesc();
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable Long id) {
        return feedbackRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}