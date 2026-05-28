package com.urbanreport.controller;

import com.urbanreport.dto.SignalementDTO;
import com.urbanreport.entity.*;
import com.urbanreport.repository.*;
import com.urbanreport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.urbanreport.entity.Intervention;
import com.urbanreport.repository.InterventionRepository;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final NotificationRepository  notificationRepository;
    private final SignalementRepository   signalementRepository;
    private final UserRepository          userRepository;
    private final UserService             userService;
    private final InterventionRepository interventionRepository;
    private static final String SUPER_ADMIN_EMAIL = "admin@urbanreport.ma";

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    }

    private boolean isSuperAdmin(User user) {
        return user.getRole() == User.Role.SUPER_ADMIN;
    }

    // ── Signalements ──────────────────────────────────────

    @GetMapping("/signalements")
    public ResponseEntity<List<SignalementDTO>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        User current = getCurrentUser(userDetails);
        List<Signalement> list;

        if (isSuperAdmin(current)) {
            list = signalementRepository.findAllByOrderByCreatedAtDesc();
        } else {
            String zone = current.getZone();
            list = signalementRepository.findAllByOrderByCreatedAtDesc()
                    .stream()
                    .filter(s -> zone != null && s.getAdresse() != null &&
                            s.getAdresse().toLowerCase().contains(zone.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(list.stream().map(SignalementDTO::fromEntity).collect(Collectors.toList()));
    }

    @PutMapping("/signalements/{id}/statut")
    public ResponseEntity<SignalementDTO> updateStatut(
            @PathVariable Long id,
            @RequestParam String statut,
            @RequestParam(required = false) String commentaire,
            @AuthenticationPrincipal UserDetails userDetails) {

        Signalement s = signalementRepository.findById(id).orElseThrow();
        User admin = getCurrentUser(userDetails);
        Signalement.Statut ancienStatut = s.getStatut();
        Signalement.Statut nouveauStatut = Signalement.Statut.valueOf(statut);

        s.setStatut(nouveauStatut);
        signalementRepository.save(s);

        // تسجيل الـ intervention
        Intervention intervention = Intervention.builder()
                .signalement(s)
                .admin(admin)
                .ancienStatut(ancienStatut)
                .nouveauStatut(nouveauStatut)
                .commentaire(commentaire)
                .build();
        interventionRepository.save(intervention);

        // notification للمستخدم
        if (s.getUser() != null && !ancienStatut.name().equals(statut)) {
            String msg = "Le statut de \"" + s.getTitre() + "\" a changé : "
                    + ancienStatut + " → " + statut;
            notificationRepository.save(Notification.builder()
                    .message(msg)
                    .user(s.getUser())
                    .signalement(s)
                    .build());
        }

        return ResponseEntity.ok(SignalementDTO.fromEntity(s));
    }

    @DeleteMapping("/signalements/{id}")
    public ResponseEntity<?> deleteSignalement(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User current = getCurrentUser(userDetails);
        if (!isSuperAdmin(current)) {
            return ResponseEntity.status(403).body("Seul le Super Admin peut supprimer");
        }
        signalementRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/signalements/filtre")
    public ResponseEntity<List<SignalementDTO>> filtrer(
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String categorie,
            @AuthenticationPrincipal UserDetails userDetails) {
        User current = getCurrentUser(userDetails);
        List<Signalement> result;

        if (statut != null)
            result = signalementRepository.findByStatut(Signalement.Statut.valueOf(statut));
        else if (categorie != null)
            result = signalementRepository.findByCategorie(Signalement.Categorie.valueOf(categorie));
        else
            result = signalementRepository.findAllByOrderByCreatedAtDesc();

        if (!isSuperAdmin(current) && current.getZone() != null) {
            String zone = current.getZone();
            result = result.stream()
                    .filter(s -> s.getAdresse() != null &&
                            s.getAdresse().toLowerCase().contains(zone.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(result.stream().map(SignalementDTO::fromEntity).collect(Collectors.toList()));
    }

    // ── Utilisateurs ──────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(
            @AuthenticationPrincipal UserDetails userDetails) {
        User current = getCurrentUser(userDetails);

        List<User> users;
        if (isSuperAdmin(current)) {
            users = userService.getAllUsers();
        } else {
            String zone = current.getZone();
            users = userService.getAllUsers().stream()
                    .filter(u -> u.getRole() == User.Role.USER &&
                            zone != null)
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users/admin")
    public ResponseEntity<?> createAdmin(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        User current = getCurrentUser(userDetails);
        if (!isSuperAdmin(current)) {
            return ResponseEntity.status(403).body("Seul le Super Admin peut créer des admins");
        }
        if (userRepository.existsByEmail(body.get("email"))) {
            return ResponseEntity.status(400).body("Email déjà utilisé");
        }
        User newAdmin = User.builder()
                .nom(body.get("nom"))
                .prenom(body.get("prenom"))
                .email(body.get("email"))
                .password(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                        .encode(body.get("password")))
                .role(User.Role.ADMIN_VILLE)
                .zone(body.get("zone"))
                .build();
        userRepository.save(newAdmin);
        return ResponseEntity.ok(Map.of("message", "Admin créé", "zone", body.get("zone")));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateRole(
            @PathVariable Long id,
            @RequestParam String role,
            @AuthenticationPrincipal UserDetails userDetails) {
        User current = getCurrentUser(userDetails);
        if (!isSuperAdmin(current)) {
            return ResponseEntity.status(403).body("Seul le Super Admin peut modifier les rôles");
        }
        User target = userRepository.findById(id).orElseThrow();
        if (target.getEmail().equals(SUPER_ADMIN_EMAIL)) {
            return ResponseEntity.status(403).body("Impossible de modifier le Super Admin");
        }
        target.setRole(User.Role.valueOf(role));
        return ResponseEntity.ok(userRepository.save(target));
    }

    @PutMapping("/users/{id}/zone")
    public ResponseEntity<?> updateZone(
            @PathVariable Long id,
            @RequestParam String zone,
            @AuthenticationPrincipal UserDetails userDetails) {
        User current = getCurrentUser(userDetails);
        if (!isSuperAdmin(current)) {
            return ResponseEntity.status(403).body("Seul le Super Admin peut modifier les zones");
        }
        User target = userRepository.findById(id).orElseThrow();
        target.setZone(zone);
        return ResponseEntity.ok(userRepository.save(target));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User current = getCurrentUser(userDetails);
        User target = userRepository.findById(id).orElseThrow();

        if (target.getEmail().equals(SUPER_ADMIN_EMAIL)) {
            return ResponseEntity.status(403).body("Impossible de supprimer le Super Admin");
        }
        if (!isSuperAdmin(current) && target.getRole() == User.Role.ADMIN_VILLE) {
            return ResponseEntity.status(403).body("Seul le Super Admin peut supprimer un Admin");
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ── Statistiques ──────────────────────────────────────

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        User current = getCurrentUser(userDetails);

        List<Signalement> signalements;
        if (isSuperAdmin(current)) {
            signalements = signalementRepository.findAllByOrderByCreatedAtDesc();
        } else {
            String zone = current.getZone();
            signalements = signalementRepository.findAllByOrderByCreatedAtDesc()
                    .stream()
                    .filter(s -> zone != null && s.getAdresse() != null &&
                            s.getAdresse().toLowerCase().contains(zone.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(Map.of(
                "total",      (long) signalements.size(),
                "signale",    signalements.stream().filter(s -> s.getStatut() == Signalement.Statut.SIGNALE).count(),
                "enCours",    signalements.stream().filter(s -> s.getStatut() == Signalement.Statut.EN_COURS).count(),
                "resolu",     signalements.stream().filter(s -> s.getStatut() == Signalement.Statut.RESOLU).count(),
                "totalUsers", userRepository.count(),
                "zone",       current.getZone() != null ? current.getZone() : "Toutes les zones"
        ));
    }
}