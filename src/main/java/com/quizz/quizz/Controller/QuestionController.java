    package com.quizz.quizz.Controller;
    import com.quizz.quizz.Entity.Question;
    
    import com.quizz.quizz.Service.QuestionService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    
    import java.util.List;
    import java.util.Optional;
    
    @RestController
    @RequestMapping("/api/questions")
    public class QuestionController {
    
        @Autowired
        private QuestionService questionService;
    
        @GetMapping
        public ResponseEntity<List<Question>> getAllQuestions() {
            List<Question> questions = questionService.getAllQuestions();
            return new ResponseEntity<>(questions, HttpStatus.OK);
        }
    
        @GetMapping("/{id}")
        public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
            Optional<Question> question = questionService.getQuestionById(id);
            return question.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        }
    
        @PostMapping
        public Question addQuestion(@RequestBody Question question) {
            if (question.getQuiz() == null || question.getQuiz().getId() == null) {
                throw new RuntimeException("Le quiz associé ne peut pas être null");
            }
            return questionService.addQuestion(question);
        }
    
    
        @PutMapping("/{id}")
        public ResponseEntity<Question> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
            Question updatedQuestion = questionService.updateQuestion(id, question);
            return updatedQuestion != null ? new ResponseEntity<>(updatedQuestion, HttpStatus.OK) :
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    
        @DeleteMapping("/{id}")
        public ResponseEntity<String> deleteQuestion(@PathVariable Long id) {
            boolean deleted = questionService.deleteQuestion(id);
            return deleted ? ResponseEntity.ok("Question deleted successfully") :
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body("Question not found");
        }
    }
