package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.DemandeEquipement;
import com.patrimoine.backend.service.DemandeEquipementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demandes")
public class DemandeEquipementController {

    @Autowired
    private DemandeEquipementService demandeEquipementService;

    @PostMapping("/soumettre")
    public DemandeEquipement soumettreDemande(@RequestBody DemandeEquipement demande) {
        return demandeEquipementService.creerDemande(demande);
    }

    @GetMapping("/filtrer")
    public List<DemandeEquipement> filtrerDemandes(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String centre
    ) {
        return demandeEquipementService.filtrerDemandes(nom, prenom, centre);
    }

    @PutMapping("/{id}/statut")
    public DemandeEquipement mettreAJourStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody // Accepter les paramètres dans le corps
    ) {
        String statut = requestBody.get("statut");
        String commentaire = requestBody.get("commentaire");
        return demandeEquipementService.mettreAJourStatut(id, statut, commentaire);
    }
}