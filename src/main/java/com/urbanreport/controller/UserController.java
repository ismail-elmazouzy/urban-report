package com.urbanreport.controller;

import com.urbanreport.entity.User;
import com.urbanreport.repository.SignalementRepository;
import com.urbanreport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.urbanreport.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService           userService;
    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final SignalementRepository signalementRepository;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        long signalementCount = signalementRepository.countByUserEmail(user.getEmail());

        java.util.HashMap<String, Object> result = new java.util.HashMap<>();
        result.put("id",              user.getId());
        result.put("nom",             user.getNom());
        result.put("prenom",          user.getPrenom());
        result.put("email",           user.getEmail());
        result.put("role",            user.getRole().name());
        result.put("photoUrl",        user.getPhotoUrl()   != null ? user.getPhotoUrl()   : "");
        result.put("cinRecto",        user.getCinRecto()   != null ? user.getCinRecto()   : "");
        result.put("cinVerso",        user.getCinVerso()   != null ? user.getCinVerso()   : "");
        result.put("cinVerifie",      user.getCinVerifie() != null ? user.getCinVerifie() : false);
        result.put("createdAt",       user.getCreatedAt()  != null ? user.getCreatedAt().toString() : "");
        result.put("signalementCount", signalementCount);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        if (body.get("nom")    != null) user.setNom(body.get("nom"));
        if (body.get("prenom") != null) user.setPrenom(body.get("prenom"));
        if (body.get("email")  != null) user.setEmail(body.get("email"));
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        if (!passwordEncoder.matches(body.get("ancienPassword"), user.getPassword())) {
            return ResponseEntity.status(400).body("Ancien mot de passe incorrect");
        }
        user.setPassword(passwordEncoder.encode(body.get("nouveauPassword")));
        userRepository.save(user);
        return ResponseEntity.ok("Mot de passe modifié");
    }

    @PostMapping(value = "/profile/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePhoto(
            @RequestParam MultipartFile photo,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
            Files.createDirectories(Paths.get(uploadDir));
            String extension = ".jpg";
            String originalName = photo.getOriginalFilename();
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            String filename = "profile_" + UUID.randomUUID() + extension;
            Files.write(Paths.get(uploadDir + filename), photo.getBytes());
            String photoUrl = "/uploads/" + filename;

            User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
            user.setPhotoUrl(photoUrl);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("photoUrl", photoUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur upload");
        }
    }

    // ── Upload CIN ────────────────────────────────────────
    @PostMapping(value = "/cin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCin(
            @RequestParam MultipartFile recto,
            @RequestParam MultipartFile verso,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
            Files.createDirectories(Paths.get(uploadDir));

            String extR = getExt(recto.getOriginalFilename());
            String extV = getExt(verso.getOriginalFilename());

            String rectoFile = "cin_recto_" + UUID.randomUUID() + extR;
            String versoFile = "cin_verso_" + UUID.randomUUID() + extV;

            Files.write(Paths.get(uploadDir + rectoFile), recto.getBytes());
            Files.write(Paths.get(uploadDir + versoFile), verso.getBytes());

            User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
            user.setCinRecto("/uploads/" + rectoFile);
            user.setCinVerso("/uploads/" + versoFile);
            user.setCinVerifie(false);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "cinRecto", user.getCinRecto(),
                    "cinVerso", user.getCinVerso()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur upload CIN");
        }
    }

    // ── Vérifier CIN (Super Admin) ────────────────────────
    @PutMapping("/{id}/cin/verifier")
    public ResponseEntity<?> verifierCin(
            @PathVariable Long id,
            @RequestParam boolean verifie) {
        User user = userRepository.findById(id).orElseThrow();
        user.setCinVerifie(verifie);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("cinVerifie", verifie));
    }

    private String getExt(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return ".jpg";
    }
}