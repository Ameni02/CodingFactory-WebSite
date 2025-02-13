package com.crud.Repositories;

import com.crud.Entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository


public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByFormationId(Long formationId);
}

