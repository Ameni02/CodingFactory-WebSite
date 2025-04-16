package org.esprit.gestion_user.Controller;

import lombok.RequiredArgsConstructor;
import org.esprit.gestion_user.Models.User;
import org.esprit.gestion_user.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ✅ Get User by ID (Only USER role can access)
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        String authenticatedEmail = getAuthenticatedUserEmail();
        Optional<User> user = userService.getUserById(id);

        if (user.isPresent() && user.get().getEmail().equals(authenticatedEmail)) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(403).build(); // Forbidden if user ID does not match
        }
    }

    // ✅ Update User Profile (Only USER role can update their own profile)
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User updatedUser) {
        String authenticatedEmail = getAuthenticatedUserEmail();
        Optional<User> existingUser = userService.getUserById(id);

        if (existingUser.isPresent() && existingUser.get().getEmail().equals(authenticatedEmail)) {
            User updated = userService.updateUser(id, updatedUser);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    // ✅ Upload Profile Picture (Only USER role can upload)
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadProfilePicture(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        String authenticatedEmail = getAuthenticatedUserEmail();
        Optional<User> user = userService.getUserById(id);

        // ✅ Log the incoming request
        System.out.println("📸 Upload request received for User ID: " + id);
        System.out.println("📧 Authenticated User: " + authenticatedEmail);

        if (!user.isPresent()) {
            System.out.println("❌ User not found for ID: " + id);
            return ResponseEntity.status(404).body("User not found");
        }

        if (!user.get().getEmail().equals(authenticatedEmail)) {
            System.out.println("❌ Unauthorized: User " + authenticatedEmail + " is not allowed to modify " + user.get().getEmail());
            return ResponseEntity.status(403).body("Unauthorized");
        }

        if (file == null || file.isEmpty()) {
            System.out.println("⚠️ File is missing or empty!");
            return ResponseEntity.badRequest().body("File is required!");
        }

        try {
            String imageUrl = userService.saveProfilePicture(file, id);
            System.out.println("✅ File uploaded successfully! URL: " + imageUrl);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            System.out.println("❌ Error uploading file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error uploading file");
        }
    }


    // ✅ Helper method to get authenticated user's email
    private String getAuthenticatedUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }
}
