package com.patrimoine.backend.entity;

import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String phone;
    private String city;

    @Enumerated(EnumType.STRING)
    private Role role = Role.ADHERANT;

    public enum Role {
        ADHERANT, RESPONSABLE, DIRECTEUR;
    }


    // ✅ Constructeurs
    public Utilisateur() {}

    public Utilisateur(Long id, String nom, String prenom, String email, String password, String phone, String city, Role role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.city = city;
        this.role = role;
    }

    // ✅ Getters
    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getCity() {
        return city;
    }

    public Role getRole() {
        return role;
    }

    // ✅ Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // ✅ Méthode pour encoder le mot de passe
    public void encodePassword() {
        if (this.password != null && !this.password.isEmpty() && !this.password.startsWith("$2a$")) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            this.password = encoder.encode(this.password);
        }
    }

    // ✅ Méthode toString (utile pour les logs)
    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", city='" + city + '\'' +
                ", role=" + role +
                '}';
    }
}
