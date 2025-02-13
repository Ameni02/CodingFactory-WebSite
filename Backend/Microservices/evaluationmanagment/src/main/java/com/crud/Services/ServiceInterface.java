package com.crud.Services;

import com.crud.Entities.Question;
import com.crud.Entities.Quiz;
import com.crud.Entities.Score;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public interface ServiceInterface {
    //gestionquiz
    Quiz createQuiz(Quiz quiz);

    // Récupérer tous les quiz
    List<Quiz> getAllQuizzes();

    // Récupérer les quiz d'une formation spécifique
    List<Quiz> getQuizzesByFormation(Long formationId);

    // Mettre à jour un quiz
    Quiz updateQuiz(Long quizId, Quiz quizDetails);

    // Supprimer un quiz
    void deleteQuiz(Long quizId);

    //gestion question
    // Créer une nouvelle question
    Question createQuestion(Question question);

    // Récupérer toutes les questions pour un quiz spécifique
    List<Question> getQuestionsByQuiz(Long quizId);

    // Mettre à jour une question
    Question updateQuestion(Long questionId, Question questionDetails);

    // Supprimer une question
    void deleteQuestion(Long questionId);

    // Sauvegarder un score
    Score saveScore(Score score);

    // Récupérer les scores d'un étudiant spécifique
    List<Score> getScoresByEtudiant(Long etudiantId);
}



