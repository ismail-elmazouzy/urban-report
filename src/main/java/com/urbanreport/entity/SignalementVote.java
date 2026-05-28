package com.urbanreport.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "signalement_votes",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "signalement_id"}
        ))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignalementVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signalement_id", nullable = false)
    private Signalement signalement;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}