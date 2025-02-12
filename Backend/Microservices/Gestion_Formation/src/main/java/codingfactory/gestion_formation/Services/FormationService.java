package codingfactory.gestion_formation.Services;

import codingfactory.gestion_formation.Entities.Formation;
import codingfactory.gestion_formation.Repositories.FormationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FormationService {
    private final FormationRepository formationRepository;

    public FormationService(FormationRepository formationRepository) {
        this.formationRepository = formationRepository;
    }

    public List<Formation> getAllFormations() {
        return formationRepository.findAll();
    }

    public Formation getFormationById(Long id) {
        return formationRepository.findById(id).orElseThrow();
    }

    public Formation createFormation(Formation formation) {
        return formationRepository.save(formation);
    }

    public Formation updateFormation(Long id, Formation formation) {
        Formation existingFormation = formationRepository.findById(id).orElseThrow();
        existingFormation.setTitre(formation.getTitre());
        existingFormation.setDescription(formation.getDescription());
        existingFormation.setDuree(formation.getDuree());
        existingFormation.setPrerequis(formation.getPrerequis());
        existingFormation.setPrice (formation.getPrice());
        return formationRepository.save(existingFormation);
    }

    public void deleteFormation(Long id) {
        formationRepository.deleteById(id);
    }
}
