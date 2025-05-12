package com.quizz.quizz.Service;

import com.quizz.quizz.Dto.QuizResultDto;
import com.quizz.quizz.Entity.QuizResult;
import com.quizz.quizz.Repository.QuizResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizResultService {

    private final QuizResultRepository quizResultRepository;

    public QuizResultService(QuizResultRepository quizResultRepository) {
        this.quizResultRepository = quizResultRepository;
    }

    public List<QuizResultDto> getQuizResultsByQuizId(Long quizId){
        return quizResultRepository.findByQuizId(quizId)
                .stream()
                .map(QuizResultDto::new)
                .collect(Collectors.toList());
    }

    public List<QuizResultDto> getQuizResultsByQuizIdAndUserId(Long userId, Long quizId){
        return quizResultRepository.findByQuizIdAndUserId(userId, quizId)
                .stream()
                .map(QuizResultDto::new)
                .collect(Collectors.toList());
    }


    public List<QuizResultDto> findQuizResultsByUserId(Long userId){
        return quizResultRepository.findByUserId(userId)
                .stream()
                .map(QuizResultDto::new)
                .collect(Collectors.toList());
    }
}
