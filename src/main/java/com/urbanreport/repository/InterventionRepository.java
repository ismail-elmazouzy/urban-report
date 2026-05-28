package com.urbanreport.repository;

import com.urbanreport.entity.Intervention;
import com.urbanreport.entity.Signalement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InterventionRepository extends JpaRepository<Intervention, Long> {
    List<Intervention> findBySignalementOrderByCreatedAtDesc(Signalement signalement);
}