package com.patrimoine.backend.repository;

import com.patrimoine.backend.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByVilleCentreOrderByDateFeedbackDesc(String villeCentre);

    @Query("SELECT f FROM Feedback f WHERE f.demande.id = :demandeId")
    Feedback findByDemandeId(@Param("demandeId") Long demandeId);

    @Query("SELECT f FROM Feedback f WHERE f.equipmentId = :equipmentId ORDER BY f.dateFeedback DESC")
    List<Feedback> findByEquipmentId(@Param("equipmentId") String equipmentId);

    List<Feedback> findAllByOrderByDateFeedbackDesc();
}