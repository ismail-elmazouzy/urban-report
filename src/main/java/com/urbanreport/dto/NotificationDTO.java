package com.urbanreport.dto;

import com.urbanreport.entity.Notification;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Long id;
    private String message;
    private boolean lu;
    private Long signalementId;
    private String signalementTitre;
    private LocalDateTime createdAt;

    public static NotificationDTO fromEntity(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(n.getId());
        dto.setMessage(n.getMessage());
        dto.setLu(n.isLu());
        dto.setCreatedAt(n.getCreatedAt());
        if (n.getSignalement() != null) {
            dto.setSignalementId(n.getSignalement().getId());
            dto.setSignalementTitre(n.getSignalement().getTitre());
        }
        return dto;
    }
}