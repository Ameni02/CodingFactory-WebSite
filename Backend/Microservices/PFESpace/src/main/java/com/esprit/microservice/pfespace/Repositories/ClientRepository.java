package com.esprit.microservice.pfespace.Repositories;

import com.esprit.microservice.pfespace.Entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client,Long> {
}
