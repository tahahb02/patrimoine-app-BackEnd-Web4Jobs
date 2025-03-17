package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permet les requêtes CORS depuis React
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email);

        // Vérification du mot de passe
        if (utilisateur == null || !passwordEncoder.matches(password, utilisateur.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Email ou mot de passe incorrect."));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Connexion réussie",
                "role", utilisateur.getRole(),
                "id", utilisateur.getId(),
                "email", utilisateur.getEmail()
        ));
    }
}
