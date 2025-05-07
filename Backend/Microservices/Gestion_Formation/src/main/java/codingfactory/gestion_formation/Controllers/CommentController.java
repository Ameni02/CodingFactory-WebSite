package codingfactory.gestion_formation.Controllers;

import codingfactory.gestion_formation.DTOs.CommentDTO;
import codingfactory.gestion_formation.Entities.Comment;
import codingfactory.gestion_formation.Entities.Formation;
import codingfactory.gestion_formation.Services.CommentService;
import codingfactory.gestion_formation.Services.FormationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private FormationService formationService;

    /**
     * Create a new comment for a formation
     */
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody @jakarta.validation.Valid CommentDTO commentDTO) {
        try {
            // Debug logging
            System.out.println("Received commentDTO: " + commentDTO);

            // Check if commentDTO is null
            if (commentDTO == null) {
                return ResponseEntity.badRequest().body("Comment data cannot be null");
            }

            // Check if formationId is null
            if (commentDTO.getFormationId() == null) {
                return ResponseEntity.badRequest().body("Formation ID is required");
            }

            // Validate formation exists
            Long formationId = commentDTO.getFormationId();
            Optional<Formation> formationOpt = formationService.getFormationById(formationId);

            if (!formationOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Formation not found with id: " + formationId);
            }

            // Create comment entity from DTO
            Comment comment = new Comment();
            comment.setContent(commentDTO.getContent());
            comment.setRating(commentDTO.getRating());
            comment.setUserName(commentDTO.getUserName());
            comment.setCategory(commentDTO.getCategory());
            comment.setCreatedAt(LocalDateTime.now());
            comment.setFormation(formationOpt.get());

            // Create comment with sentiment analysis
            Comment savedComment = commentService.createComment(comment);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating comment: Null pointer exception - " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating comment: " + e.getMessage());
        }
    }

    /**
     * Get all comments for a formation
     */
    @GetMapping("/formation/{formationId}")
    public ResponseEntity<?> getCommentsByFormationId(
            @PathVariable Long formationId,
            @RequestParam(required = false) String sentiment) {
        try {
            // Validate formation exists
            Optional<Formation> formationOpt = formationService.getFormationById(formationId);

            if (!formationOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Formation not found with id: " + formationId);
            }

            List<Comment> comments;

            // Filter by sentiment if provided
            if (sentiment != null && !sentiment.isEmpty()) {
                // Validate sentiment value
                if (!sentiment.equals("Positive") && !sentiment.equals("Neutral") && !sentiment.equals("Negative")) {
                    return ResponseEntity.badRequest().body("Invalid sentiment value. Must be 'Positive', 'Neutral', or 'Negative'");
                }

                comments = commentService.getCommentsByFormationIdAndSentiment(formationId, sentiment);
            } else {
                comments = commentService.getCommentsByFormationId(formationId);
            }

            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving comments: " + e.getMessage());
        }
    }

    /**
     * Get a comment by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable Long id) {
        try {
            Optional<Comment> commentOpt = commentService.getCommentById(id);

            if (!commentOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(commentOpt.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving comment: " + e.getMessage());
        }
    }

    /**
     * Update a comment
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody Comment commentDetails) {
        try {
            Comment updatedComment = commentService.updateComment(id, commentDetails);
            return ResponseEntity.ok(updatedComment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating comment: " + e.getMessage());
        }
    }

    /**
     * Delete a comment
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting comment: " + e.getMessage());
        }
    }

    /**
     * Batch analyze all comments for a formation
     */
    @PostMapping("/formation/{formationId}/analyze")
    public ResponseEntity<?> batchAnalyzeComments(@PathVariable Long formationId) {
        try {
            // Validate formation exists
            Optional<Formation> formationOpt = formationService.getFormationById(formationId);

            if (!formationOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Formation not found with id: " + formationId);
            }

            commentService.batchAnalyzeComments(formationId);

            return ResponseEntity.ok().body(Map.of("message", "Comments analyzed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error analyzing comments: " + e.getMessage());
        }
    }

    /**
     * Test endpoint for comment creation
     */
    @PostMapping("/test")
    public ResponseEntity<?> testCommentCreation(@RequestBody CommentDTO commentDTO) {
        try {
            // Log the received DTO
            System.out.println("Received CommentDTO: " + commentDTO);

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("content", commentDTO.getContent());
            response.put("rating", commentDTO.getRating());
            response.put("userName", commentDTO.getUserName());
            response.put("category", commentDTO.getCategory());
            response.put("formationId", commentDTO.getFormationId());
            response.put("formationIdPresent", commentDTO.getFormationId() != null);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error in test endpoint: " + e.getMessage());
        }
    }

    /**
     * Manual comment creation endpoint
     */
    @PostMapping("/manual")
    public ResponseEntity<?> createCommentManually(
            @RequestParam("content") String content,
            @RequestParam("rating") Integer rating,
            @RequestParam("userName") String userName,
            @RequestParam("category") String category,
            @RequestParam("formationId") Long formationId) {

        try {
            // Validate formation exists
            Optional<Formation> formationOpt = formationService.getFormationById(formationId);

            if (!formationOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Formation not found with id: " + formationId);
            }

            // Create comment object
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setRating(rating);
            comment.setUserName(userName);
            comment.setCategory(category);
            comment.setCreatedAt(LocalDateTime.now());
            comment.setFormation(formationOpt.get());

            // Create comment with sentiment analysis
            Comment savedComment = commentService.createComment(comment);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating comment manually: " + e.getMessage());
        }
    }
}
