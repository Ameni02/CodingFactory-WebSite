package codingfactory.gestion_formation.Repositories;

import codingfactory.gestion_formation.Entities.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {
    List<Formation> findByArchived(boolean archived);
}
