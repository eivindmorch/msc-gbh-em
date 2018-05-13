package core.training.algorithms.NSGA2;

import com.sun.javaws.exceptions.InvalidArgumentException;
import core.BtreeAlt.TempTask;
import core.data.DataSet;
import core.data.DataRow;
import core.btree.genops.Crossover;
import core.btree.genops.Mutator;
import core.training.Population;
import core.training.algorithms.Algorithm;
import core.util.ToStringBuilder;
import core.visualisation.graphing.GraphFrame;
import core.visualisation.graphing.GraphTab;
import core.visualisation.graphing.Grapher;
import core.util.SystemUtil;
import core.visualisation.plotting.Plotter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeoutException;

import static core.training.algorithms.NSGA2.NSGA2Chromosome.nonDominationRankAndCrowdingDistanceComparator;
import static core.training.algorithms.NSGA2.NSGA2Chromosome.singleObjectiveComparator;

public class NSGA2<D extends DataRow> extends Algorithm<D, NSGA2Chromosome>{

    private final Logger logger = LoggerFactory.getLogger(NSGA2.class);

    private final int INITIAL_POPULATION_SIZE;
    private final int POPULATION_SIZE;
    private final double CROSSOVER_RATE;
    private final double MUTATION_RATE;
    private final int MINIMUM_TREE_SIZE;
    private final int MAXIMUM_TREE_SIZE;

    public NSGA2(int initialPopulationSize, int populationSize, double crossoverRate, double mutationRate, int minimumTreeSize, int maximumTreeSize) {
        this.INITIAL_POPULATION_SIZE = initialPopulationSize;
        this.POPULATION_SIZE = populationSize;
        this.CROSSOVER_RATE = crossoverRate;
        this.MUTATION_RATE = mutationRate;
        this.MINIMUM_TREE_SIZE = minimumTreeSize;
        this.MAXIMUM_TREE_SIZE = maximumTreeSize;
    }

    @Override
    public void setup() {
        try {
            population = Population.generateRandomPopulation(
                    trainer.getUnitToTrainClass(),
                    NSGA2Chromosome.class,
                    INITIAL_POPULATION_SIZE,
                    MINIMUM_TREE_SIZE,
                    MAXIMUM_TREE_SIZE
            );
        } catch (InvalidArgumentException | TimeoutException e) {
            e.printStackTrace();
        }
//        population = Population.generateTestPopulation(NSGA2Chromosome.class, INITIAL_POPULATION_SIZE);
    }

    @Override
    public void step(int epoch, List<DataSet<D>> exampleDataSets) {

        if (epoch == 0) {
            trainer.simulatePopulation(population, exampleDataSets);
            trainer.setFitness(population, epoch);
            rankPopulationByNonDomination(population);
            outputInitialPopulation(population);

            trainer.updateFitnessHistory(population);
            return;
        }

        Population<NSGA2Chromosome> oldPopulation = new Population<>(population);

        // Create, evaluate and add offspring
        Population<NSGA2Chromosome> offspring = createOffspringPopulation(population, epoch);
        trainer.simulatePopulation(offspring, exampleDataSets);
        trainer.setFitness(offspring, epoch);
        population.addAll(offspring);

        // Rank population
        ArrayList<ArrayList<NSGA2Chromosome>> rankedPopulation = rankPopulationByNonDomination(population);

        // Select new population
        population = selectNewPopulationFromRankedPopulation(rankedPopulation);

        trainer.updateFitnessHistory(population);

        // Output
        output(epoch, oldPopulation, offspring, rankedPopulation, population);
    }

    private String getRanksAsString(ArrayList<ArrayList<NSGA2Chromosome>> rankedPopulation) {
        ToStringBuilder toStringBuilder = ToStringBuilder.toStringBuilder(rankedPopulation);
        for (ArrayList<NSGA2Chromosome> rank : rankedPopulation) {
            toStringBuilder.addListedElement(rank.size() + " " + rank.toString());
        }
        return toStringBuilder.toString();
    }

    @Override
    public void cleanup() {

    }

    private Population<NSGA2Chromosome> createOffspringPopulation(Population<NSGA2Chromosome> population, int epoch) {

        Population<NSGA2Chromosome> offspringPopulation = new Population<>();
        while (offspringPopulation.getSize() < population.getSize()) {

            // TODO
            NSGA2Chromosome parent1 = population.selectionTournament(2, nonDominationRankAndCrowdingDistanceComparator());
            NSGA2Chromosome parent2 = population.selectionTournament(2, nonDominationRankAndCrowdingDistanceComparator());

            // TODO Fix use of crossoverRate and mutationRate
            TempTask newRoot;
            if (SystemUtil.random.nextDouble() < CROSSOVER_RATE) {
                newRoot = Crossover.crossover(parent1.getBehaviourTreeRoot(), parent2.getBehaviourTreeRoot());
            } else {
                newRoot = Mutator.mutate(parent1.getBehaviourTreeRoot(), trainer.getUnitToTrainClass(), epoch);
            }
            if (!population.containsChromosomeWithEqualTree(newRoot)
                    && !offspringPopulation.containsChromosomeWithEqualTree(newRoot)
                    && newRoot.getSize() <= MAXIMUM_TREE_SIZE
                    && newRoot.getSize() >= MINIMUM_TREE_SIZE) {
                offspringPopulation.add(new NSGA2Chromosome(newRoot));
            }
        }
        return offspringPopulation;
    }

    private ArrayList<ArrayList<NSGA2Chromosome>> rankPopulationByNonDomination(Population<NSGA2Chromosome> population) {

        Population<NSGA2Chromosome> populationCopy = new Population<>(population);

        // Generate numOfDominators and domnates set
        for (NSGA2Chromosome c1 : populationCopy.getChromosomes()) {
            c1.dominates = new HashSet<>();
            c1.numOfDominators = 0;
            for (NSGA2Chromosome c2 : populationCopy.getChromosomes()) {
                if (c1.dominates(c2)) {
                    c1.dominates.add(c2);
                } else if (c2.dominates(c1)) {
                    c1.numOfDominators++;
                }
            }
        }
        ArrayList<ArrayList<NSGA2Chromosome>> rankedPopulation = new ArrayList<>();

        while (populationCopy.getSize() > 0) {
            ArrayList<NSGA2Chromosome> rank = new ArrayList<>();

            for (NSGA2Chromosome chromosome : populationCopy.getChromosomes()) {
                if(chromosome.numOfDominators == 0){
                    chromosome.rank = rankedPopulation.size();
                    rank.add(chromosome);
                }
            }
            populationCopy.removeAll(rank);

            for (NSGA2Chromosome dominator : rank) {
                for (NSGA2Chromosome dominated : dominator.dominates) {
                    dominated.numOfDominators--;
                }
            }
            assignCrowdingDistance(rank);
            rankedPopulation.add(rank);
        }
        return rankedPopulation;
    }


    private Population<NSGA2Chromosome> selectNewPopulationFromRankedPopulation(ArrayList<ArrayList<NSGA2Chromosome>> rankedPopulation) {
        Population<NSGA2Chromosome> newPopulation = new Population<>();

        for (ArrayList<NSGA2Chromosome> rank : rankedPopulation) {
            assignCrowdingDistance(rank);
            if (rank.size() <= POPULATION_SIZE - newPopulation.getSize()) {
                newPopulation.addAll(rank);
            }
            else {
                ArrayList<NSGA2Chromosome> rankCopy = new ArrayList<>(rank);
                rankCopy.sort(NSGA2Chromosome.crowdingDistanceComparator());
                while (newPopulation.getSize() < POPULATION_SIZE) {
                    newPopulation.add(rankCopy.remove(0));
                }
            }
        }
        return newPopulation;
    }

    private void assignCrowdingDistance(ArrayList<NSGA2Chromosome> rank) {
        for (NSGA2Chromosome chromosome : rank) {
            chromosome.crowdingDistance = 0;
        }

        for (String fitnessKey : rank.get(0).getFitness().keySet()) {

            rank.sort(NSGA2Chromosome.singleObjectiveComparator(fitnessKey));

            rank.get(0).crowdingDistance = Double.POSITIVE_INFINITY;
            rank.get(rank.size() - 1).crowdingDistance = Double.POSITIVE_INFINITY;

            double span = Math.abs(rank.get(rank.size() - 1).getFitness().get(fitnessKey) - rank.get(0).getFitness().get(fitnessKey));
            for (int i = 1; i < rank.size() - 1; i++) {
                rank.get(i).crowdingDistance += Math.abs(rank.get(i+1).getFitness().get(fitnessKey) - rank.get(i-1).getFitness().get(fitnessKey)) / span;
            }
        }
    }

    private void outputInitialPopulation(Population<NSGA2Chromosome> population) {

        Population<NSGA2Chromosome> populationClone = new Population<>(population);
        populationClone.sort(singleObjectiveComparator("Scenario 0"));

        logger.debug("INITIAL POPULATION\n" + populationClone);

        Grapher.closeAllGraphs();
        GraphFrame graphFrame = Grapher.createNewFrame("Initial population");
        graphFrame.addTab(new GraphTab("Initial population").add(populationClone));
        graphFrame.display();
    }

    private void output(
            int epoch,
            Population<NSGA2Chromosome> oldPopulation,
            Population<NSGA2Chromosome> offspring,
            ArrayList<ArrayList<NSGA2Chromosome>> rankedPopulation,
            Population<NSGA2Chromosome> newPopulation
    ) {

        Population<NSGA2Chromosome> oldPopulationClone = new Population<>(oldPopulation);
        Population<NSGA2Chromosome> offspringClone = new Population<>(offspring);
        ArrayList<ArrayList<NSGA2Chromosome>> rankedPopulationClone = new ArrayList<>(rankedPopulation);
        Population<NSGA2Chromosome> newPopulationClone = new Population<>(newPopulation);

        oldPopulationClone.sort(singleObjectiveComparator("Scenario 0"));
        offspringClone.sort(singleObjectiveComparator("Scenario 0"));
        newPopulationClone.sort(singleObjectiveComparator("Scenario 0"));

        logger.debug("OLD POPULATION\n" + oldPopulationClone);
        logger.debug("OFFSPRING\n" + offspringClone);
        logger.debug("RANKS\n" + getRanksAsString(rankedPopulationClone));
        logger.debug("NEW POPULATION\n" + newPopulationClone);

        Grapher.closeAllGraphs();
        GraphFrame graphFrame = Grapher.createNewFrame("Epoch " + epoch);
        graphFrame.addTab(new GraphTab("Old population").add(oldPopulationClone));
        graphFrame.addTab(new GraphTab("Offspring").add(offspringClone));
        graphFrame.addTab(new GraphTab("New population").add(newPopulationClone));
        graphFrame.addTab(new GraphTab("Non-dominated").add(rankedPopulation.get(0)));

        GraphTab plotTab = new GraphTab("Plot");
        for (String fitnessKey : trainer.fitnessHistoryCollections.keySet()) {
            plotTab.add(Plotter.getPlot(fitnessKey, trainer.fitnessHistoryCollections.get(fitnessKey), "Epoch", "Fitness", true));
        }
//        plotTab.add(trainer.getParetoPlot(rankedPopulation, "Size", "Scenario 0"));
        graphFrame.addTab(plotTab, true);

        graphFrame.display();
    }
}
