package com.quizz.quizz.Dto;

import java.util.List;

// QuizSubmissionRequest.java
public class QuizSubmissionRequest {
    private List<AnswerSubmission> answers;
    private int timeSpent;

    // Getters & Setters
    public List<AnswerSubmission> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerSubmission> answers) {
        this.answers = answers;
    }

    public int getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(int timeSpent) {
        this.timeSpent = timeSpent;
    }
}
