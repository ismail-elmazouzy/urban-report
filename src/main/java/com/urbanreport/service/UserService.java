package com.urbanreport.service;

import com.urbanreport.entity.User;
import com.urbanreport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User updateRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setRole(User.Role.valueOf(role));
        return userRepository.save(user);
    }
}