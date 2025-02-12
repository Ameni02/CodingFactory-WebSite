package codingfactory.gestion_formation.Repositories;

import codingfactory.gestion_formation.Entities.Formation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormationRepository extends JpaRepository<Formation, Long> {
}
