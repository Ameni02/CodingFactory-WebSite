package com.quizz.quizz.Service;

import com.quizz.quizz.Entity.Options;
import com.quizz.quizz.Repository.OptionsRepository;
import com.quizz.quizz.Repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class OptionsService {
    @Autowired
    private final OptionsRepository optionRepository;
    @Autowired
    private final QuestionRepository questionRepository;

    public OptionsService(OptionsRepository optionRepository, QuestionRepository questionRepository) {
        this.optionRepository = optionRepository;
        this.questionRepository = questionRepository;
    }

    public List<Options> getAllOptions() {
        return optionRepository.findAll();
    }

    public Optional<Options> getOptionById(Long id) {
        return optionRepository.findById(id);
    }

    public Options createOption(Options option) {
        return optionRepository.save(option);
    }

    public Options updateOption(Long id, Options option) {
        if (optionRepository.existsById(id)) {
            option.setId(id);
            return optionRepository.save(option);
        }
        return null;
    }

    public boolean deleteOption(Long id) {
        if (optionRepository.existsById(id)) {
            optionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
