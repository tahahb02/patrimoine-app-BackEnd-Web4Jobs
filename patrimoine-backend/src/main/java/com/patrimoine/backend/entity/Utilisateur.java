package com.patrimoine.backend.entity;

import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "utilisateur")
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
    @Column(name = "ville_centre")
    private VilleCentre villeCentre;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    private Role role = Role.ADHERANT;

    public enum Role {
        ADHERANT,
        RESPONSABLE,
        DIRECTEUR,
        ADMIN,
        RESPONSABLE_PATRIMOINE
    }

    public enum VilleCentre {
        TINGHIR,
        TEMARA,
        TCHAD,
        ESSAOUIRA,
        DAKHLA,
        LAAYOUNE,
        NADOR,
        AIN_EL_AOUDA;


        @Override
        public String toString() {
            return this.name().charAt(0) + this.name().substring(1).toLowerCase().replace("_", " ");
        }
    }

    public Utilisateur() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public VilleCentre getVilleCentre() { return villeCentre; }
    public void setVilleCentre(VilleCentre villeCentre) { this.villeCentre = villeCentre; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public void encodePassword() {
        if (this.password != null && !this.password.isEmpty() && !this.password.startsWith("$2a$")) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            this.password = encoder.encode(this.password);
        }
    }
}