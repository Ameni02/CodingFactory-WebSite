package com.quizz.quizz.Service;

import com.quizz.quizz.Entity.Formation;
import com.quizz.quizz.Repository.FormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormationService {

    @Autowired
    private final FormationRepository formationRepository;

    public FormationService(FormationRepository formationRepository) {
        this.formationRepository = formationRepository;
    }

    public Formation createFormation(Formation fomation) {
        return formationRepository.save(fomation);
    }
}
