package org.esprit.gestion_user.Service;

import lombok.RequiredArgsConstructor;
import org.esprit.gestion_user.Models.User;
import org.esprit.gestion_user.Repositories.UserRepo;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final JavaMailSender mailSender;

    private final String UPLOAD_DIR = "uploads/";

    // ✅ Ensure the upload directory exists
    private void ensureUploadDirExists() {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    // ✅ Get all users
    public List<User> GetAllUser()
    {
        List<User> userList=userRepo.findAll();
        return userList;
    }

    // ✅ Find user by ID
    public Optional<User> getUserById(Integer id) {
        return userRepo.findById(id);
    }

    // ✅ Update user profile
    public User updateUser(Integer userId, User updatedUser) {
        return userRepo.findById(userId).map(user -> {
            user.setFirstname(updatedUser.getFirstname());
            user.setLastname(updatedUser.getLastname());
            user.setEmail(updatedUser.getEmail());
            user.setDateOfBirth(updatedUser.getDateOfBirth());
            return userRepo.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ Upload profile picture
    public String saveProfilePicture(MultipartFile file, Integer userId) throws IOException {
        // Ensure upload directory exists
        ensureUploadDirExists();

        // Get user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate a unique file name
        String filename = user.getUsername() + "_" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, filename);

        // Save file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Update user profile picture URL
        user.setProfilePicUrl("/uploads/" + filename);
        userRepo.save(user);

        return user.getProfilePicUrl();
    }
    public void sendUnbanRequestEmail(User user) {
        String adminEmail = "kalaiamine203@gmail.com"; // Change this to your admin email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminEmail);
        message.setSubject("🔔 Demande de débanissement");
        message.setText("L'utilisateur " + user.getEmail() + " demande à être déban. Veuillez examiner sa demande.");

        mailSender.send(message);
    }
}
