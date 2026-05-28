package com.urbanreport.controller;

import com.urbanreport.dto.NotificationDTO;
import com.urbanreport.entity.*;
import com.urbanreport.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getMine(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(
                notificationRepository.findByUserOrderByCreatedAtDesc(user)
                        .stream().map(NotificationDTO::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countUnread(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(notificationRepository.countByUserAndLuFalse(user));
    }

    @PutMapping("/{id}/lu")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setLu(true);
            notificationRepository.save(n);
        });
        return ResponseEntity.ok().build();
    }

    @PutMapping("/tout-lire")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        List<Notification> notifs = notificationRepository.findByUserAndLuFalse(user);
        notifs.forEach(n -> n.setLu(true));
        notificationRepository.saveAll(notifs);
        return ResponseEntity.ok().build();
    }
}