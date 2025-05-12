package com.quizz.quizz.Dto;

import java.util.List;

public record QuizWithIdDto(
        Long id, // ✅ Include quiz ID
        String title,
        String description,
        List<QuestionWithIdDto> questions
) {
    public record QuestionWithIdDto(
            Long id, // ✅ Include question ID
            String text,
            String type,
            List<OptionWithIdDto> options
    ) {}

    public record OptionWithIdDto(
            Long id, // ✅ Include option ID
            String text,
            boolean isCorrect
    ) {}
}
