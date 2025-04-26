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


    public List<DemandeEquipement> getDemandesEnAttenteByVilleCentre(String villeCentre) {
        return demandeEquipementRepository.findDemandesEnAttenteByVilleCentre(villeCentre);
    }

    public List<DemandeEquipement> getDemandesByVilleCentre(String villeCentre) {
        return demandeEquipementRepository.findByVilleCentre(villeCentre);
    }

    public List<DemandeEquipement> getHistoriqueDemandesByVilleCentre(String villeCentre) {
        return demandeEquipementRepository.findByStatutNotAndVilleCentre("EN_ATTENTE", villeCentre);
    }

    public List<DemandeEquipement> getDemandesUrgentesByVilleCentre(String villeCentre) {
        return demandeEquipementRepository.findDemandesUrgentesByVilleCentre(villeCentre);
    }

    public List<DemandeEquipement> getDemandesEnRetardByVilleCentre(String villeCentre) {
        return demandeEquipementRepository.findDemandesEnRetardByVilleCentre(villeCentre);
    }

    public DemandeEquipement mettreAJourStatut(Long id, String statut, String commentaire, String villeCentre) {
        DemandeEquipement demande = demandeEquipementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée"));

        if (villeCentre != null && !demande.getVilleCentre().equals(villeCentre)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier cette demande");
        }

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

    public List<DemandeEquipement> getDemandesLivraisonAujourdhuiByVilleCentre(String villeCentre) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        return demandeEquipementRepository.findByDateDebutBetweenAndStatutAndVilleCentre(
                startOfDay, endOfDay, "ACCEPTEE", villeCentre);
    }

    public List<DemandeEquipement> getDemandesRetourAujourdhuiByVilleCentre(String villeCentre) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        return demandeEquipementRepository.findByDateFinBetweenAndStatutAndVilleCentre(
                startOfDay, endOfDay, "ACCEPTEE", villeCentre);
    }

    public DemandeEquipement validerLivraison(Long id, String villeCentre) {
        DemandeEquipement demande = demandeEquipementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée"));

        if (villeCentre != null && !demande.getVilleCentre().equals(villeCentre)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier cette demande");
        }

        demande.setStatut("LIVREE");
        return demandeEquipementRepository.save(demande);
    }

    public DemandeEquipement validerRetour(Long id, String villeCentre) {
        DemandeEquipement demande = demandeEquipementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée"));

        if (villeCentre != null && !demande.getVilleCentre().equals(villeCentre)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier cette demande");
        }

        demande.setStatut("RETOURNEE");
        return demandeEquipementRepository.save(demande);
    }
}