package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.DemandeEquipement;
import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.service.DemandeEquipementService;
import com.patrimoine.backend.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "http://localhost:3000")
public class DemandeEquipementController {

    @Autowired
    private DemandeEquipementService demandeEquipementService;

    @Autowired
    private UtilisateurService utilisateurService;

    @PostMapping("/soumettre/{userId}")
    public ResponseEntity<?> soumettreDemande(
            @PathVariable Long userId,
            @RequestBody DemandeEquipement demande) {

        Utilisateur utilisateur = utilisateurService.getUtilisateurById(userId);
        if (utilisateur == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }

        if (demande.getUrgence() == null || !List.of("NORMALE", "MOYENNE", "ELEVEE").contains(demande.getUrgence())) {
            demande.setUrgence("MOYENNE");
        }

        // Définir les valeurs automatiques
        demande.setNom(utilisateur.getNom());
        demande.setPrenom(utilisateur.getPrenom());
        demande.setNumeroTelephone(utilisateur.getPhone());
        demande.setDateDemande(LocalDateTime.now());
        demande.setUtilisateur(utilisateur);

        // Définir la ville du centre (vous pouvez adapter cette logique selon vos besoins)
        if (demande.getVilleCentre() == null) {
            // Option 1: Utiliser la ville de l'utilisateur si disponible
            // demande.setVilleCentre(utilisateur.getVille());

            // Option 2: Utiliser une valeur par défaut
            demande.setVilleCentre("Paris"); // Remplacez par votre valeur par défaut
        }

        try {
            DemandeEquipement savedDemande = demandeEquipementService.creerDemande(demande);
            return ResponseEntity.ok(savedDemande);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Les autres méthodes restent inchangées
    @GetMapping("/utilisateur/{userId}")
    public ResponseEntity<List<DemandeEquipement>> getDemandesByUser(@PathVariable Long userId) {
        List<DemandeEquipement> demandes = demandeEquipementService.getDemandesByUtilisateur(userId);
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/en-attente")
    public ResponseEntity<List<DemandeEquipement>> getDemandesEnAttente() {
        List<DemandeEquipement> demandes = demandeEquipementService.getDemandesEnAttente();
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/historique")
    public ResponseEntity<List<DemandeEquipement>> getHistoriqueDemandes() {
        List<DemandeEquipement> demandes = demandeEquipementService.getHistoriqueDemandes();
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/urgentes")
    public ResponseEntity<List<DemandeEquipement>> getDemandesUrgentes() {
        List<DemandeEquipement> demandes = demandeEquipementService.getDemandesUrgentes();
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/en-retard")
    public ResponseEntity<List<DemandeEquipement>> getDemandesEnRetard() {
        List<DemandeEquipement> demandes = demandeEquipementService.getDemandesEnRetard();
        return ResponseEntity.ok(demandes);
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<?> mettreAJourStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String statut = request.get("statut");
            String commentaire = request.get("commentaire");
            DemandeEquipement updated = demandeEquipementService.mettreAJourStatut(id, statut, commentaire);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/livraisons-aujourdhui")
    public ResponseEntity<List<DemandeEquipement>> getDemandesLivraisonAujourdhui() {
        List<DemandeEquipement> demandes = demandeEquipementService.getDemandesLivraisonAujourdhui();
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/retours-aujourdhui")
    public ResponseEntity<List<DemandeEquipement>> getDemandesRetourAujourdhui() {
        List<DemandeEquipement> demandes = demandeEquipementService.getDemandesRetourAujourdhui();
        return ResponseEntity.ok(demandes);
    }

    @PostMapping("/{id}/valider-livraison")
    public ResponseEntity<?> validerLivraison(@PathVariable Long id) {
        try {
            DemandeEquipement updated = demandeEquipementService.validerLivraison(id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/valider-retour")
    public ResponseEntity<?> validerRetour(@PathVariable Long id) {
        try {
            DemandeEquipement updated = demandeEquipementService.validerRetour(id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}