package com.patrimoine.backend.service;

import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public void deleteUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
    }

    public Utilisateur updateUtilisateur(Long id, Utilisateur utilisateurDetails) {
        Utilisateur utilisateur = utilisateurRepository.findById(id).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        utilisateur.setNom(utilisateurDetails.getNom());
        utilisateur.setPrenom(utilisateurDetails.getPrenom());
        utilisateur.setEmail(utilisateurDetails.getEmail());
        utilisateur.setPassword(utilisateurDetails.getPassword());
        utilisateur.setPhone(utilisateurDetails.getPhone());
        utilisateur.setCity(utilisateurDetails.getCity());
        utilisateur.setRole(utilisateurDetails.getRole());
        return utilisateurRepository.save(utilisateur);
    }
}