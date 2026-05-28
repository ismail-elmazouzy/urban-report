package com.urbanreport.dto;

import com.urbanreport.entity.Signalement;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SignalementDTO {

    private Long id;
    private String titre;
    private String description;
    private String categorie;
    private String statut;
    private Double latitude;
    private Double longitude;
    private String adresse;
    private String photoUrl;
    private String userEmail;
    private String userNom;
    private LocalDateTime createdAt;

    public static SignalementDTO fromEntity(Signalement s) {
        SignalementDTO dto = new SignalementDTO();
        dto.setId(s.getId());
        dto.setTitre(s.getTitre());
        dto.setDescription(s.getDescription());
        if (s.getCategorie() != null)
            dto.setCategorie(s.getCategorie().name());
        if (s.getStatut() != null)
            dto.setStatut(s.getStatut().name());
        dto.setLatitude(s.getLatitude());
        dto.setLongitude(s.getLongitude());
        dto.setAdresse(s.getAdresse());
        dto.setPhotoUrl(s.getPhotoUrl());
        dto.setCreatedAt(s.getCreatedAt());
        if (s.getUser() != null) {
            dto.setUserEmail(s.getUser().getEmail());
            dto.setUserNom(s.getUser().getNom() + " " + s.getUser().getPrenom());
        }
        return dto;
    }
}