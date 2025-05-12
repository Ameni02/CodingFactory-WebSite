package com.quizz.quizz.Repository;

import com.quizz.quizz.Entity.Quiz;
import com.quizz.quizz.Entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByUserId(Long userId);
    List<QuizResult> findByQuizId(Long quizId);


    List<QuizResult> findByQuizIdAndUserId(Long userId, Long quizId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.user.id = :userId")
    List<QuizResult> findQuizResultsByUserId(@Param("userId") Long userId);


    @Query("SELECT qr.quiz.id FROM QuizResult qr ORDER BY qr.score ASC")
    List<Long> findAllQuizIdsOrderedByScoreAsc();
}

