package org.esprit.gestion_user.Repositories;

import org.esprit.gestion_user.Models.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepo extends JpaRepository<Token,Integer> {
    Optional<Token>findByToken(String Token);
}
