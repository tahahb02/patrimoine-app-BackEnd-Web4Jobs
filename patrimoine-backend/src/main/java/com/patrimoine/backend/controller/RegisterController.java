package com.patrimoine.backend.controller;

import com.patrimoine.backend.entity.Utilisateur;
import com.patrimoine.backend.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
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
            @RequestParam String city,
            @RequestParam Utilisateur.VilleCentre villeCentre) {

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setEmail(email);
        utilisateur.setPassword(password);
        utilisateur.setPhone(phone);
        utilisateur.setCity(city);
        utilisateur.setVilleCentre(villeCentre);
        utilisateur.setRole(Utilisateur.Role.ADHERANT);

        utilisateur.encodePassword();
        return utilisateurService.saveUtilisateur(utilisateur);
    }


}