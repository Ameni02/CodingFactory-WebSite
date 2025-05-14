package com.esprit.microservice.pfespace.Services;

import com.esprit.microservice.pfespace.Entities.Consultant;
import com.esprit.microservice.pfespace.Repositories.ConsultantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultantService {
    @Autowired
    private ConsultantRepository consultantRepository;
    public Consultant saveConsultant(Consultant consultant) {
        return consultantRepository.save(consultant);
    }

    // Get all Consultants
    public List<Consultant> getAllConsultants() {
        return consultantRepository.findAll();
    }

    // Get Consultant by ID
    public Optional<Consultant> getConsultantById(Long id) {
        return consultantRepository.findById(id);
    }

    // Get Consultant by Email
    public Consultant getConsultantByEmail(String email) {
        return consultantRepository.findByEmail(email);
    }

    // Delete Consultant by ID
    public void deleteConsultant(Long id) {
        consultantRepository.deleteById(id);
    }
}
