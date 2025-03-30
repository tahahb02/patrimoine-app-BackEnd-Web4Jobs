package com.patrimoine.backend.service;

import com.patrimoine.backend.entity.DemandeEquipement;
import com.patrimoine.backend.repository.DemandeEquipementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DemandeEquipementService {

    @Autowired
    private DemandeEquipementRepository demandeEquipementRepository;

    public DemandeEquipement creerDemande(DemandeEquipement demande) {
        if (demande.getDateDebut() == null || demande.getDateFin() == null
                || demande.getDateDebut().isAfter(demande.getDateFin())) {
            throw new IllegalArgumentException("Les dates de réservation sont invalides");
        }

        demande.setStatut("EN_ATTENTE");
        return demandeEquipementRepository.save(demande);
    }

    public List<DemandeEquipement> getDemandesByUtilisateur(Long userId) {
        return demandeEquipementRepository.findByUtilisateurId(userId);
    }

    public List<DemandeEquipement> getDemandesEnAttente() {
        return demandeEquipementRepository.findByStatut("EN_ATTENTE");
    }

    public List<DemandeEquipement> getHistoriqueDemandes() {
        return demandeEquipementRepository.findByStatutNot("EN_ATTENTE");
    }

    public List<DemandeEquipement> filtrerDemandes(String nom, String prenom, String centre) {
        return demandeEquipementRepository.filtrerDemandes(nom, prenom, centre);
    }



    public DemandeEquipement mettreAJourStatut(Long id, String statut, String commentaire) {
        DemandeEquipement demande = demandeEquipementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        demande.setStatut(statut);
        demande.setCommentaireResponsable(commentaire);

        // Enregistrer la date de réponse seulement si ce n'est pas déjà fait
        if (demande.getDateReponse() == null && !"EN_ATTENTE".equals(statut)) {
            demande.setDateReponse(LocalDateTime.now());
        }

        return demandeEquipementRepository.save(demande);
    }
}