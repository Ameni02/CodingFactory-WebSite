package com.quizz.quizz.Dto;

import com.quizz.quizz.Entity.QuizResult;

public class QuizResultDto {
    private Long id;
    private int score;
    private String username;
    private int questionCount;
    private Long timeSpent; // ✅ temps en secondes (ou ms)

    public QuizResultDto() {
    }

    public QuizResultDto(QuizResult result) {
        this.id = result.getId();
        this.score = result.getScore();
        this.username = result.getUser().getUsername();
        this.questionCount = result.getQuiz().getQuestions().size();
        this.timeSpent = result.getTimeSpent(); // ✅ prend la durée
    }

    public Long getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public String getUsername() {
        return username;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public Long getTimeSpent() {
        return timeSpent;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public void setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
    }
}
