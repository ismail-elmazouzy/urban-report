package com.urbanreport.repository;

import com.urbanreport.entity.Signalement;
import com.urbanreport.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SignalementRepository extends JpaRepository<Signalement, Long> {
    long countByUserEmail(String email);
    List<Signalement> findAllByOrderByCreatedAtDesc();
    List<Signalement> findByUserOrderByCreatedAtDesc(User user);
    List<Signalement> findByStatut(Signalement.Statut statut);
    List<Signalement> findByCategorie(Signalement.Categorie categorie);
}