package com.quizz.quizz.Dto;

import com.quizz.quizz.Entity.Quiz;
import com.quizz.quizz.Entity.Question;
import com.quizz.quizz.Entity.Options;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class QuizResponseDto {
    private Long id;
    private String title;
    private String description;
    private boolean isVerified;
    private List<QuestionDto> questions;

    public QuizResponseDto(Long id, String title, String description, boolean isVerified, List<QuestionDto> questions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.isVerified = isVerified;
        this.questions = questions;
    }

    public static QuizResponseDto fromEntity(Quiz quiz) {
        List<QuestionDto> questionDtos = quiz.getQuestions() != null
                ? quiz.getQuestions().stream()
                .map(QuestionDto::fromEntity)
                .collect(Collectors.toList())
                : Collections.emptyList(); // ✅ Prevents `null` issues

        return new QuizResponseDto(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.isVerified(),
                questionDtos
        );
    }

    public static class QuestionDto {
        private Long id;
        private String text;
        private String type;
        private List<OptionDto> options;

        public QuestionDto(Long id, String text, String type, List<OptionDto> options) {
            this.id = id;
            this.text = text;
            this.type = type;
            this.options = options;
        }

        public static QuestionDto fromEntity(Question question) {
            List<OptionDto> optionDtos = question.getOptions() != null
                    ? question.getOptions().stream()
                    .map(OptionDto::fromEntity)
                    .collect(Collectors.toList())
                    : Collections.emptyList(); // ✅ Prevents `null` issues

            return new QuestionDto(
                    question.getId(),
                    question.getText(),
                    question.getType(),
                    optionDtos
            );
        }
    }

    public static class OptionDto {
        private Long id;
        private String text;
        private boolean isCorrect;

        public OptionDto(Long id, String text, boolean isCorrect) {
            this.id = id;
            this.text = text;
            this.isCorrect = isCorrect;
        }

        public static OptionDto fromEntity(Options option) {
            return new OptionDto(
                    option.getId(),
                    option.getText(),
                    option.isCorrect()
            );
        }
    }
}
