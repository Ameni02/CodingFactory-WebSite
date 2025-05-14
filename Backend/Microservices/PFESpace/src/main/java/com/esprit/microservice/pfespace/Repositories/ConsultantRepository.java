package com.esprit.microservice.pfespace.Repositories;

import com.esprit.microservice.pfespace.Entities.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultantRepository extends JpaRepository<Consultant, Long> {
    List<Consultant> findBySpecialtyIgnoreCase(String specialty);
    Consultant findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);

}
