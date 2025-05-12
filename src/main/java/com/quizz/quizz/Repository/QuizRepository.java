package com.quizz.quizz.Repository;

import com.quizz.quizz.Entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions")
    List<Quiz> findAllWithQuestions();

    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions qn LEFT JOIN FETCH qn.options")
    List<Quiz> findAllWithQuestionsAndOptions();

}
