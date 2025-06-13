package com.patrimoine.backend.repository;

import com.patrimoine.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUtilisateurIdOrderByDateCreationDesc(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.utilisateur.id = :userId AND n.type IN :types ORDER BY n.dateCreation DESC")
    List<Notification> findByUtilisateurIdAndTypeInOrderByDateCreationDesc(
            @Param("userId") Long userId,
            @Param("types") List<String> types);

    @Query("SELECT n FROM Notification n WHERE n.utilisateur.id = :userId AND n.type = :type ORDER BY n.dateCreation DESC")
    List<Notification> findByUtilisateurIdAndTypeOrderByDateCreationDesc(
            @Param("userId") Long userId,
            @Param("type") String type);

    List<Notification> findByUtilisateurIdAndLueFalseOrderByDateCreationDesc(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.utilisateur.id = :userId AND n.lue = false AND n.type IN :types ORDER BY n.dateCreation DESC")
    List<Notification> findByUtilisateurIdAndLueFalseAndTypeInOrderByDateCreationDesc(
            @Param("userId") Long userId,
            @Param("types") List<String> types);

    @Query("SELECT n FROM Notification n WHERE n.utilisateur.id = :userId AND n.lue = false AND n.type = :type ORDER BY n.dateCreation DESC")
    List<Notification> findByUtilisateurIdAndLueFalseAndTypeOrderByDateCreationDesc(
            @Param("userId") Long userId,
            @Param("type") String type);

    Optional<Notification> findByLink(String link);

    @Modifying
    @Query("UPDATE Notification n SET n.lue = true WHERE n.utilisateur.id = :userId AND n.lue = false")
    void markAllAsReadByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.utilisateur.id = :userId AND n.lue = false")
    long countUnreadByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.utilisateur.id = :userId AND n.lue = false AND n.type IN :types")
    long countUnreadByUserAndTypeIn(
            @Param("userId") Long userId,
            @Param("types") List<String> types);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.utilisateur.id = :userId AND n.lue = false AND n.type = :type")
    long countUnreadByUserAndType(
            @Param("userId") Long userId,
            @Param("type") String type);
}