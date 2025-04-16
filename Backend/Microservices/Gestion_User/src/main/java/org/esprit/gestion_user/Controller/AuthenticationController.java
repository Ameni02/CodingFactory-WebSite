package org.esprit.gestion_user.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.esprit.gestion_user.Models.RegistrationRequest;
import org.esprit.gestion_user.Models.User;
import org.esprit.gestion_user.Repositories.UserRepo;
import org.esprit.gestion_user.Service.AuthenticationRequest;
import org.esprit.gestion_user.Service.AuthenticationResponse;
import org.esprit.gestion_user.Service.AuthenticationService;
import org.esprit.gestion_user.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
@Slf4j
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") // Or your Angular app URL
@Tag(name = "Authentication")
public class AuthenticationController {
    private final AuthenticationService service;
    private final UserRepo userRepository;
    private final UserService userService;


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException {
        service.register(request);
        return ResponseEntity.accepted().build();
    }
    @PostMapping("authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
    @GetMapping("/activate-account")
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        service.activateAccount(token);
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return ResponseEntity.ok(service.refreshAccessToken(refreshToken));
    }
    // ✅ Request Password Reset (Sends Email with Reset Token)
    @PostMapping("/request-password-reset")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) throws MessagingException {
        service.requestPasswordReset(email);
        return ResponseEntity.ok("Password reset link has been sent to your email.");
    }

    // ✅ Reset Password (User Submits New Password)
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (token == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token and new password are required."));
        }

        service.resetPassword(token, newPassword);
        return ResponseEntity.ok(Map.of("message", "Your password has been successfully reset."));
    }
    @PostMapping("/request-unban")
    public ResponseEntity<Map<String, String>> requestUnban(@RequestParam String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.isEnabled()) { // User is banned
                userService.sendUnbanRequestEmail(user);
                log.info("Unban request sent for user with email {}", email);
                return ResponseEntity.ok(Map.of("message", "Unban request sent successfully!"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Your account is already active."));
            }
        }
        return ResponseEntity.badRequest().body(Map.of("message", "User not found!"));
    }


}
