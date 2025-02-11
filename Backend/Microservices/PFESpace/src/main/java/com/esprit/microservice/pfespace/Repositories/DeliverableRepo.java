package com.esprit.microservice.pfespace.Repositories;

import com.esprit.microservice.pfespace.Entities.Deliverable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliverableRepo extends JpaRepository<Deliverable, Long> {
}
