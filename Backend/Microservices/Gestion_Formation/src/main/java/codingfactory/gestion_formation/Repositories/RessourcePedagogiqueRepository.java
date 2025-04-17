package codingfactory.gestion_formation.Repositories;

import codingfactory.gestion_formation.Entities.RessourcePedagogique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface RessourcePedagogiqueRepository  extends JpaRepository<RessourcePedagogique, Long>  {
}

