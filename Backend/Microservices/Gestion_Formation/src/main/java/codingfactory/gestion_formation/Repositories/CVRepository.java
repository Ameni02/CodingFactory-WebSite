package codingfactory.gestion_formation.Repositories;

import codingfactory.gestion_formation.Entities.CV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CVRepository extends   JpaRepository<CV, Long> {
    List<CV> findByValide(boolean valide);
}

