package com.quizz.quizz.Controller;

import com.quizz.quizz.Entity.Quiz;
import com.quizz.quizz.Repository.QuizRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminQuizController {
    private final QuizRepository quizRepository;

    public AdminQuizController(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @PutMapping("/verify/{quizId}")
    public String verifyQuiz(@PathVariable Long quizId) {
        Optional<Quiz> optionalQuiz = quizRepository.findById(quizId);

        if (optionalQuiz.isPresent()) {
            Quiz quiz = optionalQuiz.get();
            quiz.setVerified(true); // ✅ Admin marks quiz as verified
            quizRepository.save(quiz);
            return "Quiz with ID " + quizId + " has been verified.";
        } else {
            return "Quiz not found.";
        }
    }
}
