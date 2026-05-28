package com.urbanreport.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "signalements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Signalement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private Categorie categorie;

    @Enumerated(EnumType.STRING)
    private Statut statut;

    private Double latitude;
    private Double longitude;
    private String adresse;
    private String photoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ElementCollection
    @CollectionTable(name = "signalement_photos", joinColumns = @JoinColumn(name = "signalement_id"))
    @Column(name = "photo_url")
    @Builder.Default
    private List<String> photos = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.statut = Statut.SIGNALE;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Statut {
        SIGNALE, EN_COURS, RESOLU
    }

    public enum Categorie {
        VOIRIE, ECLAIRAGE, DECHETS, EAU, AUTRE
    }
}