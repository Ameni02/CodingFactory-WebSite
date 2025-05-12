package com.quizz.quizz.Service;

import com.quizz.quizz.Dto.AnswerSubmission;
import com.quizz.quizz.Dto.QuizDto;
import com.quizz.quizz.Dto.QuizDtoWithoutQuestions;
import com.quizz.quizz.Dto.QuizWithIdDto;
import com.quizz.quizz.Entity.*;
import com.quizz.quizz.Repository.OptionsRepository;
import com.quizz.quizz.Repository.QuestionRepository;
import com.quizz.quizz.Repository.QuizRepository;
import com.quizz.quizz.Repository.UserRepository;
import com.quizz.quizz.Repository.QuizResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final OptionsRepository optionsRepository;
    private final QuizResultRepository quizResultRepository;
    private final UserRepository userRepository;

    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository, OptionsRepository optionsRepository, QuizResultRepository quizResultRepository,UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.optionsRepository = optionsRepository;
        this.quizResultRepository = quizResultRepository;
        this.userRepository =userRepository;
    }

    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public List<Quiz> getAllQuizzesWithQuestions() {
        return quizRepository.findAllWithQuestions();
    }

    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    public Quiz updateQuiz(Long quizId, QuizDto quizDto) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setTitle(quizDto.title());
        quiz.setDescription(quizDto.description());

        for (QuizDto.QuestionDto questionDto : quizDto.questions()) {
            Question question = questionRepository.findByText(questionDto.question())
                    .orElse(new Question());

            question.setText(questionDto.question());
            question.setType(questionDto.type());
            question.setQuiz(quiz);
            question = questionRepository.save(question);

            for (QuizDto.OptionDto optionDto : questionDto.options()) {
                Options option = optionsRepository.findByText(optionDto.text() != null ? optionDto.text() : optionDto.value())
                        .orElse(new Options());

                option.setText(optionDto.text() != null ? optionDto.text() : optionDto.value());
                option.setCorrect(optionDto.isCorrect());
                option.setQuestion(question);
                optionsRepository.save(option);
            }
        }

        return quizRepository.save(quiz);
    }

    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }

    public QuizResult calculateScoreAndSave(Long quizId, Long userId, List<AnswerSubmission> answers, int timeSpent)
    {
        int score = 0;

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        for (AnswerSubmission answer : answers) {
            if (answer.questionId() == null || answer.optionId() == null) continue;

            Options selectedOption = optionsRepository.findById(answer.optionId())
                    .orElseThrow(() -> new RuntimeException("Option non trouvée : " + answer.optionId()));

            // Vérifie que l’option appartient bien à la question
            if (!selectedOption.getQuestion().getId().equals(answer.questionId())) {
                System.out.println("❌ Option ne correspond pas à la question.");

                continue;
            }

            if (selectedOption.isCorrect()) {
                score++;
        }
        }

        QuizResult quizResult = new QuizResult();
        quizResult.setScore(score);
        quizResult.setQuiz(quiz);
        quizResult.setUser(user);
        quizResult.setTimeSpent(timeSpent);

        return quizResultRepository.save(quizResult);
    }



    public Quiz addQuizWithQuestionsAndOptions(QuizDto quizDto) {
        Quiz quiz = new Quiz();
        quiz.setTitle(quizDto.title());
        quiz.setDescription(quizDto.description());
        quiz.setVerified(quizDto.isVerified() != null ? quizDto.isVerified() : false);
        Category category = quizDto.category() ;
        quiz.setCategory(category);
        System.out.println("🔍 Catégorie : " + category);
        quiz = quizRepository.save(quiz);

        if (quizDto.questions() == null || quizDto.questions().isEmpty()) {
            System.out.println("⚠️ Aucune question reçue !");
            return quiz;
        }

        for (QuizDto.QuestionDto questionDto : quizDto.questions()) {
            System.out.println("🧠 Question : " + questionDto.question());

            Question question = new Question();
            question.setText(questionDto.question());
            question.setType(questionDto.type());
            question.setQuiz(quiz);
            question = questionRepository.save(question);

            if (questionDto.options() != null) {
                for (QuizDto.OptionDto optionDto : questionDto.options()) {
                    Options option = new Options();
                    option.setText(optionDto.text() != null ? optionDto.text() : optionDto.value());
                    option.setCorrect(optionDto.isCorrect());
                    option.setQuestion(question);
                    optionsRepository.save(option);
                }
            }
        }

        return quiz;
    }

    public List<QuizDtoWithoutQuestions> getAllQuizzesWithoutQuestions() {
        List<Quiz> quizzes = quizRepository.findAll();
        return quizzes.stream()
                .map(quiz -> new QuizDtoWithoutQuestions(quiz.getId(), quiz.getTitle(), quiz.getDescription()))
                .collect(Collectors.toList());
    }

    public QuizDto getQuizWithDetails(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<QuizDto.QuestionDto> questionsDto = questionRepository.findByQuizId(quizId).stream()
                .map(question -> new QuizDto.QuestionDto(
                        question.getText(),
                        question.getType(),
                        optionsRepository.findByQuestionId(question.getId()).stream()
                                .map(option -> new QuizDto.OptionDto(option.getText(), option.isCorrect(), null))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return new QuizDto(
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.isVerified(),
                quiz.getCategory(),
                questionsDto
        );
    }

    public QuizWithIdDto getQuizWithDetailsForUser(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<QuizWithIdDto.QuestionWithIdDto> questionsDto = questionRepository.findByQuizId(quizId).stream()
                .map(question -> new QuizWithIdDto.QuestionWithIdDto(
                        question.getId(),
                        question.getText(),
                        question.getType(),
                        optionsRepository.findByQuestionId(question.getId()).stream()
                                .map(option -> new QuizWithIdDto.OptionWithIdDto(
                                        option.getId(),
                                        option.getText(),
                                        option.isCorrect()
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return new QuizWithIdDto(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                questionsDto
        );
    }

    public List<String> getWorstThirdQuizTitles() {
        List<Long> sortedQuizIds = quizResultRepository.findAllQuizIdsOrderedByScoreAsc();

        if (sortedQuizIds.isEmpty()) return List.of();

        int third = sortedQuizIds.size() / 3    ;
        List<Long> worstIds = sortedQuizIds.subList(0, third);

        return quizRepository.findAllById(worstIds)
                .stream()
                .map(Quiz::getTitle)
                .distinct()
                .toList();
    }
}
