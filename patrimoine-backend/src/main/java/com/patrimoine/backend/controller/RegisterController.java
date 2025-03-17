package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permet les requêtes CORS depuis le frontend
@RequestMapping("/api/register")
public class RegisterController {

    @Autowired
    private UtilisateurService utilisateurService;

    @PostMapping
    public Utilisateur registerUser(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String phone,
            @RequestParam String city)

    {
    
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setEmail(email);
        utilisateur.setPassword(password);
        utilisateur.setPhone(phone);
        utilisateur.setCity(city);


        System.out.println("Utilisateur reçu dans le contrôleur: " + utilisateur); // Log de l'utilisateur reçu
        utilisateur.encodePassword();  // Assurez-vous que le mot de passe est bien encodé

        // Vérification des champs avant la sauvegarde
        System.out.println("Avant sauvegarde : " + utilisateur);

        return utilisateurService.saveUtilisateur(utilisateur);  // Sauvegarde de l'utilisateur
    }


}
