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
        if (demande.getDateDebut().isAfter(demande.getDateFin())) {
            throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début");
        }
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

    public List<DemandeEquipement> getDemandesUrgentes() {
        return demandeEquipementRepository.findDemandesUrgentes();
    }

    public List<DemandeEquipement> getDemandesEnRetard() {
        return demandeEquipementRepository.findDemandesEnRetard();
    }

    public DemandeEquipement mettreAJourStatut(Long id, String statut, String commentaire) {
        DemandeEquipement demande = demandeEquipementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée"));

        demande.setStatut(statut);
        demande.setCommentaireResponsable(commentaire);
        demande.setDateReponse(LocalDateTime.now());

        return demandeEquipementRepository.save(demande);
    }
}