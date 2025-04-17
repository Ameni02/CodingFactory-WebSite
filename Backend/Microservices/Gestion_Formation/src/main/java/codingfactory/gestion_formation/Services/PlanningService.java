package codingfactory.gestion_formation.Services;

import codingfactory.gestion_formation.Entities.CreneauHoraire;
import codingfactory.gestion_formation.Entities.Formateur;
import codingfactory.gestion_formation.Entities.Formation;
import codingfactory.gestion_formation.Repositories.FormateurRepository;
import codingfactory.gestion_formation.Repositories.FormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlanningService {
    private static final int POPULATION_SIZE = 50;
    private static final int GENERATIONS = 100;
    private static final double MUTATION_RATE = 0.1;

    @Autowired
    private FormationRepository formationRepo;

    @Autowired
    private FormateurRepository formateurRepo;

    public List<CreneauHoraire> genererPlanningOptimal() {
        List<Formateur> formateurs = formateurRepo.findAll();
        List<Formation> formations = formationRepo.findAll();

        List<List<CreneauHoraire>> population = initialiserPopulation(formateurs, formations);

        for (int i = 0; i < GENERATIONS; i++) {
            population = evoluer(population);
        }

        return meilleurIndividu(population);
    }

    private List<List<CreneauHoraire>> initialiserPopulation(List<Formateur> formateurs, List<Formation> formations) {
        List<List<CreneauHoraire>> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            List<CreneauHoraire> planning = new ArrayList<>();
            for (Formation formation : formations) {
                Formateur formateurChoisi = formation.getFormateursPossibles()
                        .get(new Random().nextInt(formation.getFormateursPossibles().size()));

                planning.add(new CreneauHoraire(null, "Lundi", "9h-11h", formateurChoisi, formation));
            }
            population.add(planning);
        }
        return population;
    }

    private List<List<CreneauHoraire>> evoluer(List<List<CreneauHoraire>> population) {
        population.sort(Comparator.comparingInt(this::evaluerPlanning));

        List<List<CreneauHoraire>> nouvellePopulation = new ArrayList<>();
        nouvellePopulation.add(population.get(0)); // Garde le meilleur planning

        while (nouvellePopulation.size() < POPULATION_SIZE) {
            List<CreneauHoraire> parent1 = population.get(new Random().nextInt(POPULATION_SIZE / 2));
            List<CreneauHoraire> parent2 = population.get(new Random().nextInt(POPULATION_SIZE / 2));

            List<CreneauHoraire> enfant = croisement(parent1, parent2);
            if (new Random().nextDouble() < MUTATION_RATE) {
                mutation(enfant);
            }

            nouvellePopulation.add(enfant);
        }

        return nouvellePopulation;
    }

    private int evaluerPlanning(List<CreneauHoraire> planning) {
        int score = 0;
        Set<String> occupes = new HashSet<>();

        for (CreneauHoraire creneau : planning) {
            String cle = creneau.getJour() + creneau.getHeure() + creneau.getFormateur().getId();
            if (occupes.contains(cle)) {
                score -= 10; // Pénalité pour double réservation
            }
            if (creneau.getFormateur().getDisponibilites().contains(creneau.getJour() + " " + creneau.getHeure())) {
                score += 5;
            }
            occupes.add(cle);
        }
        return score;
    }

    private List<CreneauHoraire> croisement(List<CreneauHoraire> parent1, List<CreneauHoraire> parent2) {
        List<CreneauHoraire> enfant = new ArrayList<>();
        for (int i = 0; i < parent1.size(); i++) {
            enfant.add(new Random().nextBoolean() ? parent1.get(i) : parent2.get(i));
        }
        return enfant;
    }

    private void mutation(List<CreneauHoraire> planning) {
        int index = new Random().nextInt(planning.size());
        CreneauHoraire creneau = planning.get(index);

        Formateur nouveauFormateur = creneau.getFormation().getFormateursPossibles()
                .get(new Random().nextInt(creneau.getFormation().getFormateursPossibles().size()));

        creneau.setFormateur(nouveauFormateur);
    }

    private List<CreneauHoraire> meilleurIndividu(List<List<CreneauHoraire>> population) {
        return population.stream()
                .max(Comparator.comparingInt(this::evaluerPlanning))
                .orElse(new ArrayList<>());
    }
}
