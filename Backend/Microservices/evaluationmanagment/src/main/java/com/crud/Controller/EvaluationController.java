package com.crud.Controller;

import com.crud.Entities.Question;
import com.crud.Entities.Quiz;
import com.crud.Entities.Score;
import com.crud.Services.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/evaluation")
@RestController

public class EvaluationController {
@Autowired
EvaluationService evaluationService;

    // Créer un nouveau quiz
    @PostMapping("/quizzes")
    public Quiz createQuiz(@RequestBody Quiz quiz) {
        return evaluationService.createQuiz(quiz);
    }

    // Récupérer tous les quiz
    @GetMapping("/quizzes")
    public List<Quiz> getAllQuizzes() {
        return evaluationService.getAllQuizzes();
    }

    // Récupérer les quiz d'une formation spécifique
    @GetMapping("/quizzes/formation/{formationId}")
    public List<Quiz> getQuizzesByFormation(@PathVariable Long formationId) {
        return evaluationService.getQuizzesByFormation(formationId);
    }

    // Mettre à jour un quiz
    @PutMapping("/quizzes/{quizId}")
    public Quiz updateQuiz(@PathVariable Long quizId, @RequestBody Quiz quizDetails) {
        return evaluationService.updateQuiz(quizId, quizDetails);
    }

    // Supprimer un quiz
    @DeleteMapping("/quizzes/{quizId}")
    public void deleteQuiz(@PathVariable Long quizId) {
        evaluationService.deleteQuiz(quizId);
    }
    // === Gestion des Questions ===

    // Ajouter une nouvelle question
    @PostMapping("/questions")
    public Question createQuestion(@RequestBody Question question) {
        return evaluationService.createQuestion(question);
    }

    // Récupérer les questions d'un quiz
    @GetMapping("/questions/quiz/{quizId}")
    public List<Question> getQuestionsByQuiz(@PathVariable Long quizId) {
        return evaluationService.getQuestionsByQuiz(quizId);
    }

    // Mettre à jour une question
    @PutMapping("/questions/{questionId}")
    public Question updateQuestion(@PathVariable Long questionId, @RequestBody Question questionDetails) {
        return evaluationService.updateQuestion(questionId, questionDetails);
    }

    // Supprimer une question
    @DeleteMapping("/questions/{questionId}")
    public void deleteQuestion(@PathVariable Long questionId) {
        evaluationService.deleteQuestion(questionId);
    }
    // Sauvegarder un score
    @PostMapping("/scores")
    public Score saveScore(@RequestBody Score score) {
        return evaluationService.saveScore(score);
    }

    // Récupérer les scores d'un étudiant
    @GetMapping("/scores/etudiant/{etudiantId}")
    public List<Score> getScoresByEtudiant(@PathVariable Long etudiantId) {
        return evaluationService.getScoresByEtudiant(etudiantId);
    }


}
