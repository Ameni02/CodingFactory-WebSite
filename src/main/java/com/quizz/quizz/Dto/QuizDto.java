package com.quizz.quizz.Dto;

import com.quizz.quizz.Entity.Category;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.List;

public record QuizDto(
        String title,
        String description,
        Boolean isVerified,
        @Enumerated(EnumType.STRING)
        Category category,
        List<QuestionDto> questions
) {

    public record QuestionDto(
            String question,        // ✅ Texte de la question
            String type,            // ✅ ➕ AJOUTER CECI
            List<OptionDto> options
    ) {}

    public record OptionDto(
            String text,
            boolean isCorrect,
            String value
    ) {}
}
