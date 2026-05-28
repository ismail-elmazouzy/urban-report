package com.urbanreport.repository;

import com.urbanreport.entity.Signalement;
import com.urbanreport.entity.SignalementVote;
import com.urbanreport.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignalementVoteRepository extends JpaRepository<SignalementVote, Long> {

    // شحال صوت على signalement معين
    long countBySignalement(Signalement signalement);

    // واش شخص معين صوت على signalement معين
    boolean existsByUserAndSignalement(User user, Signalement signalement);

    // كل التصويتات ديال شخص معين
    java.util.List<SignalementVote> findByUser(User user);
}