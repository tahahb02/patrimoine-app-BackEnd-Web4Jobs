package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.DemandeEquipement;
import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.repository.DemandeEquipementRepository;
import com.patrimoine.backend.service.DemandeEquipementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "http://localhost:3000")
public class DemandeEquipementController {

    @Autowired
    private DemandeEquipementService demandeEquipementService;

    @Autowired
    private DemandeEquipementRepository demandeEquipementRepository;

    @Autowired
    private NotificationController notificationController;

    @GetMapping("/en-attente/{villeCentre}")
    public ResponseEntity<List<DemandeEquipement>> getDemandesEnAttenteByVilleCentre(
            @PathVariable String villeCentre,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Center") String userCenter) {

        if (!"RESPONSABLE".equals(userRole) && !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if ("RESPONSABLE".equals(userRole) && !villeCentre.equals(userCenter)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<DemandeEquipement> demandes = demandeEquipementService.getDemandesEnAttenteByVilleCentre(villeCentre);
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/ville/{villeCentre}")
    public ResponseEntity<List<DemandeEquipement>> getDemandesByVilleCentre(
            @PathVariable String villeCentre,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Center") String userCenter) {

        if (!"RESPONSABLE".equals(userRole) && !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if ("RESPONSABLE".equals(userRole) && !villeCentre.equals(userCenter)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<DemandeEquipement> demandes = demandeEquipementService.getDemandesByVilleCentre(villeCentre);
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/historique/{villeCentre}")
    public ResponseEntity<List<DemandeEquipement>> getHistoriqueDemandesByVilleCentre(
            @PathVariable String villeCentre,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Center") String userCenter) {

        if (!"RESPONSABLE".equals(userRole) && !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if ("RESPONSABLE".equals(userRole) && !villeCentre.equals(userCenter)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<DemandeEquipement> demandes = demandeEquipementService.getHistoriqueDemandesByVilleCentre(villeCentre);
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/urgentes/{villeCentre}")
    public ResponseEntity<List<DemandeEquipement>> getDemandesUrgentesByVilleCentre(
            @PathVariable String villeCentre,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Center") String userCenter) {

        if (!"RESPONSABLE".equals(userRole) && !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if ("RESPONSABLE".equals(userRole) && !villeCentre.equals(userCenter)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<DemandeEquipement> demandes = demandeEquipementService.getDemandesUrgentesByVilleCentre(villeCentre);
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/en-retard/{villeCentre}")
    public ResponseEntity<List<DemandeEquipement>> getDemandesEnRetardByVilleCentre(
            @PathVariable String villeCentre,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Center") String userCenter) {

        if (!"RESPONSABLE".equals(userRole) && !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if ("RESPONSABLE".equals(userRole) && !villeCentre.equals(userCenter)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<DemandeEquipement> demandes = demandeEquipementService.getDemandesEnRetardByVilleCentre(villeCentre);
        return ResponseEntity.ok(demandes);
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<?> mettreAJourStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Center") String userCenter) {

        if (!"RESPONSABLE".equals(userRole) && !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
        }

        try {
            String statut = request.get("statut");
            String commentaire = request.get("commentaire");

            DemandeEquipement updated;
            if ("ADMIN".equals(userRole)) {
                updated = demandeEquipementService.mettreAJourStatut(id, statut, commentaire, null);
            } else {
                updated = demandeEquipementService.mettreAJourStatut(id, statut, commentaire, userCenter);
            }

            // Créer une notification de réponse pour l'adhérent
            Map<String, Object> notifRequest = new HashMap<>();
            notifRequest.put("demandeId", id);
            notifRequest.put("statut", statut);
            notifRequest.put("commentaire", commentaire);
            notificationController.createReponseNotification(notifRequest);

            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/livraisons-aujourdhui/{villeCentre}")
    public ResponseEntity<List<DemandeEquipement>> getDemandesLivraisonAujourdhuiByVilleCentre(
            @PathVariable String villeCentre,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Center") String userCenter) {

        if (!"RESPONSABLE".equals(userRole) && !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if ("RESPONSABLE".equals(userRole) && !villeCentre.equals(userCenter)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<DemandeEquipement> demandes = demandeEquipementService.getDemandesLivraisonAujourdhuiByVilleCentre(villeCentre);
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/retours-aujourdhui/{villeCentre}")
    public ResponseEntity<List<DemandeEquipement>> getDemandesRetourAujourdhuiByVilleCentre(
            @PathVariable String villeCentre,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Center") String userCenter) {

        if (!"RESPONSABLE".equals(userRole) && !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if ("RESPONSABLE".equals(userRole) && !villeCentre.equals(userCenter)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<DemandeEquipement> demandes = demandeEquipementService.getDemandesRetourAujourdhuiByVilleCentre(villeCentre);
        return ResponseEntity.ok(demandes);
    }

    @PostMapping("/{id}/valider-livraison")
    public ResponseEntity<?> validerLivraison(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Center") String userCenter) {

        if (!"RESPONSABLE".equals(userRole) && !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
        }

        try {
            DemandeEquipement updated;
            if ("ADMIN".equals(userRole)) {
                updated = demandeEquipementService.validerLivraison(id, null);
            } else {
                updated = demandeEquipementService.validerLivraison(id, userCenter);
            }
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/valider-retour")
    public ResponseEntity<?> validerRetour(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Center") String userCenter) {

        if (!"RESPONSABLE".equals(userRole) && !"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");
        }

        try {
            DemandeEquipement updated;
            if ("ADMIN".equals(userRole)) {
                updated = demandeEquipementService.validerRetour(id, null);
            } else {
                updated = demandeEquipementService.validerRetour(id, userCenter);
            }
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/soumettre/{userId}")
    public ResponseEntity<?> soumettreDemande(
            @PathVariable Long userId,
            @RequestBody DemandeEquipement demande,
            @RequestHeader("X-User-Role") String userRole) {

        if (!"ADHERANT".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Seuls les adhérents peuvent soumettre des demandes");
        }

        try {
            if (demande.getIdEquipement() == null || demande.getNomEquipement() == null) {
                return ResponseEntity.badRequest()
                        .body("Les informations sur l'équipement sont requises");
            }

            if (demande.getDateDebut() == null || demande.getDateFin() == null) {
                return ResponseEntity.badRequest()
                        .body("Les dates de début et fin sont requises");
            }

            if (demande.getDateDebut().isAfter(demande.getDateFin())) {
                return ResponseEntity.badRequest()
                        .body("La date de fin doit être postérieure à la date de début");
            }

            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setId(userId);
            demande.setUtilisateur(utilisateur);

            if (demande.getUrgence() == null) {
                demande.setUrgence("MOYENNE");
            }

            demande.setDateDemande(LocalDateTime.now());
            demande.setStatut("EN_ATTENTE");

            DemandeEquipement savedDemande = demandeEquipementService.creerDemande(demande);

            // Créer une notification pour les responsables
            Map<String, Long> request = new HashMap<>();
            request.put("demandeId", savedDemande.getId());
            notificationController.createDemandeNotification(request);

            return ResponseEntity.ok(savedDemande);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/utilisateur/{userId}/demandes")
    public ResponseEntity<List<DemandeEquipement>> getDemandesByUtilisateur(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token) {

        try {
            List<DemandeEquipement> demandes = demandeEquipementService.getDemandesByUtilisateur(userId);
            return ResponseEntity.ok(demandes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<DemandeEquipement>> getDemandesByUser(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token) {

        try {
            List<DemandeEquipement> demandes = demandeEquipementService.getDemandesByUtilisateur(userId);
            return ResponseEntity.ok(demandes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/rp/historique")
    public ResponseEntity<List<DemandeEquipement>> getHistoriqueDemandesForRP(
            @RequestHeader("X-User-Role") String userRole) {

        if (!"RESPONSABLE_PATRIMOINE".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<DemandeEquipement> demandes = demandeEquipementRepository.findAllByOrderByDateDemandeDesc();
        return ResponseEntity.ok(demandes);
    }
}