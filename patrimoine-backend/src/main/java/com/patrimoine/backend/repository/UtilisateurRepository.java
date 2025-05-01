    package com.patrimoine.backend.repository;

    import com.patrimoine.backend.entity.Utilisateur;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    import java.util.List;

    @Repository
    public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
        Utilisateur findByEmail(String email);
        List<Utilisateur> findByVilleCentre(Utilisateur.VilleCentre villeCentre);
        List<Utilisateur> findByRoleAndVilleCentre(Utilisateur.Role role, Utilisateur.VilleCentre villeCentre);
        List<Utilisateur> findByRole(Utilisateur.Role role);
    }