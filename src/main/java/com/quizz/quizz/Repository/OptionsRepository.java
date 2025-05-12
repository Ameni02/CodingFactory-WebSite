package com.quizz.quizz.Repository;

import com.quizz.quizz.Entity.Options;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface OptionsRepository  extends JpaRepository<Options, Long> {
    List<Options> findByQuestionId(Long questionId);
    Optional<Options> findByText(String text);
}

