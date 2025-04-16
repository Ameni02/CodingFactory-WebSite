package org.esprit.gestion_user.Service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.esprit.gestion_user.Models.RegistrationRequest;
import org.esprit.gestion_user.Models.Token;
import org.esprit.gestion_user.Models.User;
import org.esprit.gestion_user.Repositories.RoleRepo;
import org.esprit.gestion_user.Repositories.TokenRepo;
import org.esprit.gestion_user.Repositories.UserRepo;
import org.esprit.gestion_user.Template.EmailTemplateName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepository;
    private final TokenRepo tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        boolean isFirstUser = userRepository.count() == 0;
        var role = roleRepo.findByName(isFirstUser ? "ADMIN" : "USER")
                .orElseThrow(() -> new IllegalStateException("Required role was not initiated"));

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(role))
                .build();

        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.Fullname(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.ints(length, 0, 10)
                .mapToObj(String::valueOf)
                .reduce("", String::concat);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var jwtToken = jwtService.generateToken(userDetails, user.getId());
        var refreshToken = jwtService.generateRefreshToken(userDetails, user.getId());

        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    public AuthenticationResponse refreshAccessToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtService.isTokenValid(refreshToken, userDetails)) { // 🔹 Pass userDetails as second argument
            Integer userId = Math.toIntExact(jwtService.extractUserId(refreshToken));
            String newAccessToken = jwtService.generateToken(userDetails, userId);
            return new AuthenticationResponse(newAccessToken, refreshToken);
        } else {
            throw new RuntimeException("Invalid Refresh Token");
        }
    }


    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been sent.");
        }

        var user = savedToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
    // 1️⃣ Generate and Send Password Reset Token
    public void requestPasswordReset(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate reset token
        String resetToken = generateResetToken(6);

        // Save the token in the database
        Token token = Token.builder()
                .token(resetToken)
                .createAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15)) // Token valid for 15 mins
                .user(user)
                .build();
        tokenRepository.save(token);

        // Send reset email
        String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;
        emailService.sendEmail(
                user.getEmail(),
                user.getFirstname(),
                EmailTemplateName.RESET_PASSWORD,
                resetLink,
                resetToken,
                "Password Reset Request"
        );
    }

    // 2️⃣ Reset Password
    public void resetPassword(String token, String newPassword) {
        Token resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (LocalDateTime.now().isAfter(resetToken.getExpiresAt())) {
            throw new RuntimeException("Reset token has expired.");
        }

        // Update the user's password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Invalidate the token after use
        tokenRepository.delete(resetToken);
    }

    // Helper method to generate a random reset token
    private String generateResetToken(int length) {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.ints(length, 0, 10)
                .mapToObj(String::valueOf)
                .reduce("", String::concat);
    }
}
