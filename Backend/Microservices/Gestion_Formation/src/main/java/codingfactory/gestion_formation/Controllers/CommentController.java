package codingfactory.gestion_formation.Controllers;

import codingfactory.gestion_formation.Entities.Comment;
import codingfactory.gestion_formation.Entities.Formation;
import codingfactory.gestion_formation.Services.CommentService;
import codingfactory.gestion_formation.Services.FormationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private FormationService formationService;

    /**
     * Create a new comment for a formation
     */
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody Comment comment) {
        try {
            // Validate formation exists
            Long formationId = comment.getFormation().getId();
            Optional<Formation> formationOpt = formationService.getFormationById(formationId);
            
            if (!formationOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Formation not found with id: " + formationId);
            }
            
            // Set formation
            comment.setFormation(formationOpt.get());
            
            // Create comment with sentiment analysis
            Comment savedComment = commentService.createComment(comment);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating comment: " + e.getMessage());
        }
    }

    /**
     * Get all comments for a formation
     */
    @GetMapping("/formation/{formationId}")
    public ResponseEntity<?> getCommentsByFormationId(@PathVariable Long formationId) {
        try {
            // Validate formation exists
            Optional<Formation> formationOpt = formationService.getFormationById(formationId);
            
            if (!formationOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Formation not found with id: " + formationId);
            }
            
            List<Comment> comments = commentService.getCommentsByFormationId(formationId);
            
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
}
