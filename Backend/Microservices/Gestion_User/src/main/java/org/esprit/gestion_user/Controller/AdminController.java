package org.esprit.gestion_user.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.esprit.gestion_user.Models.User;
import org.esprit.gestion_user.Repositories.UserRepo;
import org.esprit.gestion_user.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepo userRepository;
    private final UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')") // Ensure the user has the 'ADMIN' role
    @PutMapping("/ban/{id}")
    public ResponseEntity<String> banUser(@PathVariable Integer id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setEnabled(false);
                    userRepository.save(user);
                    log.info("User with ID {} banned", id);
                    return ResponseEntity.ok("Utilisateur banni avec succès !");
                })
                .orElseGet(() -> ResponseEntity.badRequest().body("Utilisateur introuvable !"));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/unban/{id}")
    public ResponseEntity<String> unbanUser(@PathVariable Integer id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setEnabled(true);
                    userRepository.save(user);
                    log.info("User with ID {} unbanned", id);
                    return ResponseEntity.ok("Utilisateur réactivé avec succès !");
                })
                .orElseGet(() -> ResponseEntity.badRequest().body("Utilisateur introuvable !"));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("User with ID {} deleted", id);
            return ResponseEntity.ok("Utilisateur supprimé avec succès !");
        }
        return ResponseEntity.badRequest().body("Utilisateur introuvable !");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/list_user")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.GetAllUser();
        log.info("Admin fetched all users");
        return ResponseEntity.ok(users);
    }
   /* @PostMapping("/request-unban")
    public ResponseEntity<String> requestUnban(@RequestParam String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.isEnabled()) { // User is banned
                // Send email to admin
                userService.sendUnbanRequestEmail(user);
                log.info("Unban request sent for user with email {}", email);
                return ResponseEntity.ok("Demande de débanissement envoyée avec succès !");
            } else {
                return ResponseEntity.badRequest().body("Votre compte est déjà actif.");
            }
        }
        return ResponseEntity.badRequest().body("Utilisateur introuvable !");
    }*/
}

