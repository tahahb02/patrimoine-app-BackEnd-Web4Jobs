    package com.patrimoine.backend.repository;

    import com.patrimoine.backend.entity.Utilisateur;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import org.springframework.stereotype.Repository;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.List;

    @Repository
    public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
        Utilisateur findByEmail(String email);
        List<Utilisateur> findByVilleCentre(Utilisateur.VilleCentre villeCentre);
        List<Utilisateur> findByRoleAndVilleCentre(Utilisateur.Role role, Utilisateur.VilleCentre villeCentre);
        List<Utilisateur> findByRole(Utilisateur.Role role);
        List<Utilisateur> findByRoleAndVilleCentreOrRole(Utilisateur.Role role1, Utilisateur.VilleCentre villeCentre, Utilisateur.Role role2);

        long countByRole(Utilisateur.Role role);
        long countByVilleCentre(Utilisateur.VilleCentre villeCentre);
        long countByRoleAndVilleCentre(Utilisateur.Role role, Utilisateur.VilleCentre villeCentre);
        long countByCreatedAtAfter(LocalDateTime date);
        long countByRoleAndCreatedAtAfter(Utilisateur.Role role, LocalDateTime date);

        @Query("SELECT DATE(u.createdAt) as date, COUNT(u) as count " +
                "FROM Utilisateur u " +
                "WHERE u.role = 'ADHERANT' " +
                "AND DATE(u.createdAt) BETWEEN :startDate AND :endDate " +
                "GROUP BY DATE(u.createdAt) " +
                "ORDER BY DATE(u.createdAt)")
        List<Object[]> countAdherantsByPeriod(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
    }