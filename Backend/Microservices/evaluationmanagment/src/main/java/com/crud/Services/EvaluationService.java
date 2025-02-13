package com.crud.Services;

import com.crud.Entities.Question;
import com.crud.Entities.Quiz;
import com.crud.Entities.Score;
import com.crud.Repositories.QuestionRepository;
import com.crud.Repositories.QuizRepository;
import com.crud.Repositories.ScoreRepository;
import com.jetbrains.exported.JBRApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class EvaluationService implements ServiceInterface{
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired

    private ScoreRepository scoreRepository;

    //gestion quiz

    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public List<Quiz> getQuizzesByFormation(Long formationId) {
        return quizRepository.findByFormationId(formationId);
    }

    public Quiz updateQuiz(Long quizId, Quiz quizDetails) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setTitle(quizDetails.getTitle());
        quiz.setDescription(quizDetails.getDescription());
        quiz.setFormation(quizDetails.getFormation());
        return quizRepository.save(quiz);
    }

    public void deleteQuiz(Long quizId) {
        quizRepository.deleteById(quizId);
    }
    //gestionquestion
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    public List<Question> getQuestionsByQuiz(Long quizId) {
        return questionRepository.findByQuizId(quizId);
    }

    public Question updateQuestion(Long questionId, Question questionDetails) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new RuntimeException("Question not found"));
        question.setQuestionText(questionDetails.getQuestionText());
        question.setCorrectAnswer(questionDetails.getCorrectAnswer());
        question.setOptions(questionDetails.getOptions());
        return questionRepository.save(question);
    }

    public void deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }
    //gestion score
    public Score saveScore(Score score) {
        return scoreRepository.save(score);
    }

    public List<Score> getScoresByEtudiant(Long etudiantId) {
        return scoreRepository.findByEtudiantId(etudiantId);
    }

}




