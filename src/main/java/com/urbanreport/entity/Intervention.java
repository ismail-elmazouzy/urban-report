package com.urbanreport.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interventions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Intervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signalement_id", nullable = false)
    private Signalement signalement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Enumerated(EnumType.STRING)
    private Signalement.Statut ancienStatut;

    @Enumerated(EnumType.STRING)
    private Signalement.Statut nouveauStatut;

    private String commentaire;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}