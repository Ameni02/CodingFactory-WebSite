package com.quizz.quizz.Service;
import com.quizz.quizz.Entity.Question;
import com.quizz.quizz.Entity.Quiz;
import com.quizz.quizz.Repository.QuestionRepository;
import com.quizz.quizz.Repository.QuizRepository;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;


@Service
public class QuestionService {



    private final QuestionRepository questionRepository;
    private final  QuizRepository quizRepository;

    public QuestionService(QuestionRepository questionRepository, QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }


    // Récupérer toutes les questions
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // Récupérer une question par son ID
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    public Question addQuestion(Question question) {
        if (question.getQuiz() != null) {
            Quiz quiz = quizRepository.findById(question.getQuiz().getId())
                    .orElseThrow(() -> new RuntimeException("Quiz introuvable"));
            question.setQuiz(quiz);
        }
        return questionRepository.save(question);
    }

    // Modifier une question existante
    public Question updateQuestion(Long id, Question questionDetails) {
        Optional<Question> existingQuestion = questionRepository.findById(id);
        if (existingQuestion.isPresent()) {
            Question question = existingQuestion.get();
            question.setText(questionDetails.getText());
            question.setOptions(questionDetails.getOptions());
            return questionRepository.save(question);
        }
        return null;
    }

    // Supprimer une question
    public boolean deleteQuestion(Long id) {
        Optional<Question> question = questionRepository.findById(id);
        if (question.isPresent()) {
            questionRepository.delete(question.get());
            return true;
        }
        return false;
    }
}
