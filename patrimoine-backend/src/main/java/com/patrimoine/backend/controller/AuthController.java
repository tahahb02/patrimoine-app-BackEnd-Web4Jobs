package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email);
        if (utilisateur == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Email ou mot de passe incorrect."));
        }

        if (!passwordEncoder.matches(password, utilisateur.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Email ou mot de passe incorrect."));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Connexion réussie",
                "role", utilisateur.getRole().name(),
                "id", utilisateur.getId(),
                "email", utilisateur.getEmail(),
                "nom", utilisateur.getNom(),
                "prenom", utilisateur.getPrenom(),
                "phone", utilisateur.getPhone(),
                "city", utilisateur.getCity(),
                "villeCentre", utilisateur.getVilleCentre().name()
        ));
    }
}