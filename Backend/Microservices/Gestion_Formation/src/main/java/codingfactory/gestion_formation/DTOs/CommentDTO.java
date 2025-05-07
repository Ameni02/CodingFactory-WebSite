package codingfactory.gestion_formation.DTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for Comment creation
 */
public class CommentDTO {

    @NotBlank(message = "Content is required")
    private String content;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer rating;

    private String userName;

    private String category;

    @jakarta.validation.constraints.NotNull(message = "Formation ID is required")
    private Long formationId;

    // Getters and setters

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getFormationId() {
        return formationId;
    }

    public void setFormationId(Long formationId) {
        this.formationId = formationId;
    }

    @Override
    public String toString() {
        return "CommentDTO{" +
                "content='" + content + '\'' +
                ", rating=" + rating +
                ", userName='" + userName + '\'' +
                ", category='" + category + '\'' +
                ", formationId=" + formationId +
                '}';
    }
}
