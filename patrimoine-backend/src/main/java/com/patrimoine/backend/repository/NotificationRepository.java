package com.patrimoine.backend.repository;

import com.patrimoine.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Trouver les notifications par utilisateur triées par date
    List<Notification> findByUtilisateurIdOrderByDateCreationDesc(Long userId);

    // Trouver les notifications non lues par utilisateur
    List<Notification> findByUtilisateurIdAndLueFalseOrderByDateCreationDesc(Long userId);

    // Trouver les notifications par type
    List<Notification> findByTypeOrderByDateCreationDesc(String type);

    // Trouver les notifications par équipement
    List<Notification> findByEquipmentIdOrderByDateCreationDesc(String equipmentId);

    // Trouver une notification par son lien
    Optional<Notification> findByLink(String link);

    // Trouver les notifications par ID lié
    List<Notification> findByRelatedId(Long relatedId);

    // Marquer toutes les notifications d'un utilisateur comme lues
    @Modifying
    @Query("UPDATE Notification n SET n.lue = true WHERE n.utilisateur.id = :userId AND n.lue = false")
    void markAllAsReadByUser(@Param("userId") Long userId);

    // Compter les notifications non lues par utilisateur
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.utilisateur.id = :userId AND n.lue = false")
    long countUnreadByUser(@Param("userId") Long userId);

    // Trouver les notifications par centre
    @Query("SELECT n FROM Notification n WHERE n.utilisateur.villeCentre = :villeCentre ORDER BY n.dateCreation DESC")
    List<Notification> findByVilleCentre(@Param("villeCentre") String villeCentre);

    // Trouver les feedbacks récents pour un équipement
    @Query("SELECT n FROM Notification n WHERE n.type = 'FEEDBACK' AND n.equipmentId = :equipmentId ORDER BY n.dateCreation DESC")
    List<Notification> findFeedbackNotificationsForEquipment(@Param("equipmentId") String equipmentId);
}