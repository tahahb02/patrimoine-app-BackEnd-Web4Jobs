package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Long id) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurById(id);
        return utilisateur != null ? ResponseEntity.ok(utilisateur) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> updateUtilisateur(
            @PathVariable Long id,
            @RequestBody Utilisateur utilisateurDetails) {
        Utilisateur updated = utilisateurService.updateUtilisateur(id, utilisateurDetails);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPhotoProfil(
            @PathVariable Long id,
            @RequestParam("photo") MultipartFile photo) {

        try {
            if (photo.isEmpty()) {
                return ResponseEntity.badRequest().body("Aucune photo fournie");
            }

            // Vérification de la taille (2MB max)
            if (photo.getSize() > 2_000_000) {
                return ResponseEntity.badRequest().body("La taille de l'image ne doit pas dépasser 2MB");
            }

            String imageBase64 = Base64.getEncoder().encodeToString(photo.getBytes());
            utilisateurService.updateProfileImage(id, imageBase64);
            return ResponseEntity.ok("Photo mise à jour avec succès");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du traitement de l'image: " + e.getMessage());
        }
    }
}