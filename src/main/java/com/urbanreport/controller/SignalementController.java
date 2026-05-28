package com.urbanreport.controller;

import com.urbanreport.dto.SignalementDTO;
import com.urbanreport.service.SignalementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.urbanreport.entity.Notification;
import com.urbanreport.repository.NotificationRepository;
import com.urbanreport.repository.UserRepository;
import java.util.Map;
import java.util.List;
import com.urbanreport.repository.SignalementRepository;
import com.urbanreport.entity.Signalement;
import com.urbanreport.entity.User;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.http.MediaType;
import com.urbanreport.entity.SignalementVote;
import com.urbanreport.repository.SignalementVoteRepository;

@RestController
@RequestMapping("/api/signalements")
@RequiredArgsConstructor
public class SignalementController {

    private final SignalementService signalementService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SignalementRepository signalementRepository;
    private final SignalementVoteRepository signalementVoteRepository;

    @GetMapping("/{id}/votes")
    public ResponseEntity<?> getVotes(@PathVariable Long id) {
        Signalement s = signalementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Non trouvé"));
        long totalVotes = signalementVoteRepository.countBySignalement(s);
        return ResponseEntity.ok(Map.of("totalVotes", totalVotes));
    }

    @PostMapping("/{id}/rejoindre")
    public ResponseEntity<?> rejoindre(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Signalement s = signalementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Non trouvé"));
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        if (signalementVoteRepository.existsByUserAndSignalement(user, s)) {
            return ResponseEntity.status(409)
                    .body(Map.of("message", "Vous avez déjà voté pour ce signalement"));
        }

        SignalementVote vote = SignalementVote.builder()
                .user(user)
                .signalement(s)
                .build();
        signalementVoteRepository.save(vote);

        String msg = user.getNom() + " " + user.getPrenom() +
                " a le même problème que \"" + s.getTitre() + "\"";
        Notification notif = Notification.builder()
                .message(msg)
                .user(s.getUser())
                .signalement(s)
                .build();
        notificationRepository.save(notif);

        long totalVotes = signalementVoteRepository.countBySignalement(s);

        return ResponseEntity.ok(Map.of(
                "message", "Vote enregistré avec succès",
                "totalVotes", totalVotes
        ));
    }

    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addPhotos(
            @PathVariable Long id,
            @RequestParam("photos") List<MultipartFile> photos) {
        try {
            Signalement s = signalementRepository.findById(id).orElseThrow();
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
            Files.createDirectories(Paths.get(uploadDir));

            for (MultipartFile photo : photos) {
                String ext = ".jpg";
                String orig = photo.getOriginalFilename();
                if (orig != null && orig.contains(".")) {
                    ext = orig.substring(orig.lastIndexOf("."));
                }
                String filename = UUID.randomUUID() + ext;
                Files.write(Paths.get(uploadDir + filename), photo.getBytes());
                s.getPhotos().add("/uploads/" + filename);
            }
            signalementRepository.save(s);
            return ResponseEntity.ok(s.getPhotos());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur upload");
        }
    }

    @GetMapping("/public")
    public ResponseEntity<List<SignalementDTO>> getPublic() {
        return ResponseEntity.ok(signalementService.getAll());
    }

    @GetMapping
    public ResponseEntity<List<SignalementDTO>> getAll() {
        return ResponseEntity.ok(signalementService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SignalementDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(signalementService.getById(id));
    }

    @GetMapping("/mes-signalements")
    public ResponseEntity<List<SignalementDTO>> getMine(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(signalementService.getByUser(userDetails.getUsername()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SignalementDTO> create(
            @RequestParam String titre,
            @RequestParam String description,
            @RequestParam String categorie,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) String adresse,
            @RequestParam(required = false) MultipartFile photo,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                signalementService.create(titre, description, categorie,
                        latitude, longitude, adresse, photo,
                        userDetails.getUsername())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        signalementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}