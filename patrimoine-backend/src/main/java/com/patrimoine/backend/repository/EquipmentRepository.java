    package com.patrimoine.backend.repository;

    import com.patrimoine.backend.entity.Equipment;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    @Repository
    public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    }