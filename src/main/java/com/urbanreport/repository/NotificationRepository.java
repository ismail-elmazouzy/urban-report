package com.urbanreport.repository;

import com.urbanreport.entity.Notification;
import com.urbanreport.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndLuFalse(User user);
    long countByUserAndLuFalse(User user);
}