package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.DemandeEquipement;
import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.service.DemandeEquipementService;
import com.patrimoine.backend.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        // Remplir automatiquement les infos utilisateur
        demande.setNom(utilisateur.getNom());
        demande.setPrenom(utilisateur.getPrenom());
        demande.setNumeroTelephone(utilisateur.getPhone());
        demande.setUtilisateur(utilisateur);

        try {
            DemandeEquipement savedDemande = demandeEquipementService.creerDemande(demande);
            return ResponseEntity.ok(savedDemande);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/utilisateur/{userId}")
    public ResponseEntity<List<DemandeEquipement>> getDemandesByUser(@PathVariable Long userId) {
        List<DemandeEquipement> demandes = demandeEquipementService.getDemandesByUtilisateur(userId);
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/filtrer")
    public List<DemandeEquipement> filtrerDemandes(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String centre) {
        return demandeEquipementService.filtrerDemandes(nom, prenom, centre);
    }

    @PutMapping("/{id}/statut")
    public DemandeEquipement mettreAJourStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody) {
        String statut = requestBody.get("statut");
        String commentaire = requestBody.get("commentaire");
        return demandeEquipementService.mettreAJourStatut(id, statut, commentaire);
    }
}