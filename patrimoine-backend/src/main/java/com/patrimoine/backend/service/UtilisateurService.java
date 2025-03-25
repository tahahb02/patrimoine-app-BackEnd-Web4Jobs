package com.patrimoine.backend.service;

import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
        if (utilisateur.getPassword() != null) {
            utilisateur.encodePassword();
        }
        return utilisateurRepository.save(utilisateur);
    }

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public void deleteUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
    }

    public Utilisateur updateUtilisateur(Long id, Utilisateur utilisateurDetails) {
        return utilisateurRepository.findById(id)
                .map(utilisateur -> {
                    utilisateur.setNom(utilisateurDetails.getNom());
                    utilisateur.setPrenom(utilisateurDetails.getPrenom());
                    utilisateur.setEmail(utilisateurDetails.getEmail());
                    utilisateur.setPhone(utilisateurDetails.getPhone());
                    utilisateur.setCity(utilisateurDetails.getCity());
                    utilisateur.setRole(utilisateurDetails.getRole());

                    if (utilisateurDetails.getPassword() != null) {
                        utilisateur.setPassword(passwordEncoder.encode(utilisateurDetails.getPassword()));
                    }

                    return utilisateurRepository.save(utilisateur);
                })
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public Utilisateur getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id).orElse(null);
    }

    public Utilisateur updateProfileImage(Long id, String imageBase64) {
        return utilisateurRepository.findById(id)
                .map(utilisateur -> {
                    utilisateur.setProfileImage(imageBase64);
                    return utilisateurRepository.save(utilisateur);
                })
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}