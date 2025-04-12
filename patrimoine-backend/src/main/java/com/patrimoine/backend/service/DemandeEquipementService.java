package com.patrimoine.backend.service;

import com.patrimoine.backend.entity.DemandeEquipement;
import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.repository.DemandeEquipementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

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

    public List<DemandeEquipement> getHistoriqueUtilisationEquipement(String equipementId) {
        return demandeEquipementRepository.findDemandesAccepteesParEquipement(equipementId);
    }

    public Map<String, Object> getStatistiquesUtilisationEquipement(String equipementId) {
        List<Object[]> resultats = demandeEquipementRepository.findUtilisationParAdherant(equipementId);

        Map<String, Object> statistiques = new HashMap<>();
        List<Map<String, Object>> utilisations = resultats.stream()
                .map(r -> {
                    Utilisateur user = (Utilisateur) r[0];
                    Long heures = (Long) r[1];

                    Map<String, Object> utilisation = new HashMap<>();
                    utilisation.put("nom", user.getNom());
                    utilisation.put("prenom", user.getPrenom());
                    utilisation.put("email", user.getEmail());
                    utilisation.put("telephone", user.getPhone());
                    utilisation.put("heuresUtilisation", heures);

                    return utilisation;
                })
                .collect(Collectors.toList());

        statistiques.put("utilisations", utilisations);
        statistiques.put("totalUtilisations", resultats.size());

        return statistiques;
    }

    public List<DemandeEquipement> getDemandesLivraisonAujourdhui() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        return demandeEquipementRepository.findByDateDebutBetweenAndStatut(
                startOfDay, endOfDay, "ACCEPTEE");
    }

    public List<DemandeEquipement> getDemandesRetourAujourdhui() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        return demandeEquipementRepository.findByDateFinBetweenAndStatut(
                startOfDay, endOfDay, "ACCEPTEE");
    }

    public DemandeEquipement validerLivraison(Long id) {
        DemandeEquipement demande = demandeEquipementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée"));

        demande.setStatut("LIVREE");
        return demandeEquipementRepository.save(demande);
    }

    public DemandeEquipement validerRetour(Long id) {
        DemandeEquipement demande = demandeEquipementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée"));

        demande.setStatut("RETOURNEE");
        return demandeEquipementRepository.save(demande);

        // Ici vous pourriez ajouter l'envoi du feedback
    }
}