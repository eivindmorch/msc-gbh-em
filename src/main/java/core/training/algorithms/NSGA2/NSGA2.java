package core.training.algorithms.NSGA2;

import com.badlogic.gdx.ai.btree.Task;
import core.data.DataSet;
import core.data.rows.DataRow;
import core.model.btree.BehaviorTreeUtil;
import core.model.btree.genops.Crossover;
import core.model.btree.genops.Mutator;
import core.training.FitnessEvaluator;
import core.training.Population;
import core.training.algorithms.Algorithm;
import core.util.ToStringBuilder;
import core.util.graphing.Grapher;
import core.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;

import static core.training.algorithms.NSGA2.NSGA2Chromosome.nonDominationRankAndCrowdingDistanceComparator;
import static core.training.algorithms.NSGA2.NSGA2Chromosome.singleObjectiveComparator;

public class NSGA2<D extends DataRow> extends Algorithm<D, NSGA2Chromosome>{

    private final Logger logger = LoggerFactory.getLogger(NSGA2.class);

    private DataSet<D> lastExample;

    private final int POPULATION_SIZE;
    private final double CROSSOVER_RATE;
    private final double MUTATION_RATE;

    public NSGA2(Class<D> evaluationDataRowClass, FitnessEvaluator fitnessEvaluator, int populationSize,
                 double crossoverRate, double mutationRate) {
        super(evaluationDataRowClass, fitnessEvaluator);
        this.POPULATION_SIZE = populationSize;
        this.CROSSOVER_RATE = crossoverRate;
        this.MUTATION_RATE = mutationRate;
    }

    @Override
    public void setup() {
        //noinspection unchecked
        population = Population.generateRandomPopulation(POPULATION_SIZE, trainer.getUnitToTrainClass(), NSGA2Chromosome.class);
    }

    @Override
    public void step(int epoch, int exampleNumber, DataSet<D> exampleDataSet) {

        if (exampleDataSet != lastExample) {
            lastExample = exampleDataSet;

            trainer.simulatePopulation(population, exampleDataSet.getNumOfTicks(), exampleDataSet.getScenarioPath());
            setFitness(population, epoch, exampleNumber, exampleDataSet);
            rankPopulationByNonDomination(population);

            graphAndLogPopulation("Initial population", population);
        }

        // Create, evaluate and add offspring
        Population<NSGA2Chromosome> offspring = createOffspringPopulation(population);
        trainer.simulatePopulation(offspring, exampleDataSet.getNumOfTicks(), exampleDataSet.getScenarioPath());
        setFitness(offspring, epoch, exampleNumber, exampleDataSet);
        population.addAll(offspring);

        // Rank population
        ArrayList<ArrayList<NSGA2Chromosome>> rankedPopulation = rankPopulationByNonDomination(population);

        // Select new population
        Population<NSGA2Chromosome> newPopulation = selectNewPopulationFromRankedPopulation(rankedPopulation);

        Grapher.closeAllPopulationGraphs();
        graphAndLogPopulation("Old population", population);
        graphAndLogPopulation("Offspring", offspring);
        logger.debug("RANKS: " + getRanksAsString(rankedPopulation));
        graphAndLogPopulation("New population", newPopulation);

        population = newPopulation;
    }

    private void graphAndLogPopulation(String name, Population<NSGA2Chromosome> population) {
        Population<NSGA2Chromosome> populationClone = new Population<>(population);
        populationClone.sort(singleObjectiveComparator(0));
        logger.debug(name.toUpperCase() + ": " + populationClone.toString());
        Grapher.graph(name, populationClone);
    }

    private String getRanksAsString(ArrayList<ArrayList<NSGA2Chromosome>> rankedPopulation) {
        ToStringBuilder toStringBuilder = ToStringBuilder.toStringBuilder(rankedPopulation);
        for (ArrayList<NSGA2Chromosome> rank : rankedPopulation) {
            toStringBuilder.addListedElement(rank.size() + " " + rank.toString());
        }
        return toStringBuilder.toString();
    }

    private void setFitness(Population<NSGA2Chromosome> population, int epoch, int exampleNumber, DataSet<D> exampleDataSet) {
        for (int chromosomeIndex = 0; chromosomeIndex < population.getSize(); chromosomeIndex++) {
            String intraResourcesScenarioLogsPath = SystemUtil.getDataFileIntraResourcesFolderPath(epoch, exampleNumber, chromosomeIndex);
            DataSet<D> chromosomeDataSet = new DataSet<>(
                    evaluationDataRowClass,
                    intraResourcesScenarioLogsPath
                            + exampleDataSet.getUnitMarking()
                            + "/" + exampleDataSet.getDataSetName()
                            + ".csv"
            );
            ArrayList<Double> chromosomeFitness = evaluate(exampleDataSet, chromosomeDataSet);

            int size = BehaviorTreeUtil.getSize(population.get(chromosomeIndex).getBtree());
            chromosomeFitness.add((double) size);

            population.get(chromosomeIndex).setFitness(chromosomeFitness);
        }
    }

    @Override
    public void cleanup() {

    }

    private Population<NSGA2Chromosome> createOffspringPopulation(Population<NSGA2Chromosome> population) {
        Population<NSGA2Chromosome> offspringPopulation = new Population<>();
        for (int i = 0; i < population.getSize(); i++) {
            // TODO
            NSGA2Chromosome parent1 = population.selectionTournament(2, nonDominationRankAndCrowdingDistanceComparator());
            NSGA2Chromosome parent2 = population.selectionTournament(2, nonDominationRankAndCrowdingDistanceComparator());
            // TODO Fix use of crossoverRate and mutationRate
            if (SystemUtil.random.nextDouble() < CROSSOVER_RATE) {
                Task crossoverChild = Crossover.crossover(parent1.getBtree(), parent2.getBtree());
                offspringPopulation.add(new NSGA2Chromosome(crossoverChild));
            } else {
                Task mutationChild = Mutator.mutate(parent1.getBtree(), trainer.getUnitToTrainClass());
                offspringPopulation.add(new NSGA2Chromosome(mutationChild));
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
        // TODO Replace hard coded fitness number
        for (int fitnessIndex = 0; fitnessIndex < 2; fitnessIndex++) {
            rank.sort(NSGA2Chromosome.singleObjectiveComparator(fitnessIndex));

            rank.get(0).crowdingDistance = Double.POSITIVE_INFINITY;
            rank.get(rank.size() - 1).crowdingDistance = Double.POSITIVE_INFINITY;

            double span = Math.abs(rank.get(rank.size() - 1).getFitness().get(fitnessIndex) - rank.get(0).getFitness().get(fitnessIndex));
            for (int i = 1; i < rank.size() - 1; i++) {
                rank.get(i).crowdingDistance += Math.abs(rank.get(i+1).getFitness().get(fitnessIndex) - rank.get(i-1).getFitness().get(fitnessIndex)) / span;
            }
        }
    }
//    private void assignCrowdingDistance(ArrayList<NSGA2Chromosome> rank) {
//        for (NSGA2Chromosome chromosome : rank) chromosome.crowdingDistance = 0;
//        if (Settings.useOverallDeviation) assignCrowdingDistancePerObjective(rank, 0);
//        if (Settings.useEdgeValue) assignCrowdingDistancePerObjective(rank, 1);
//        if (Settings.useConnectivity) assignCrowdingDistancePerObjective(rank, 2);
//    }

//    private void assignCrowdingDistancePerObjective(ArrayList<NSGA2Chromosome> rank, int index) {
//        if (index == 0) rank.sort(NSGA2Chromosome.overallDeviationComparator());
//        else if (index == 1) rank.sort(NSGA2Chromosome.edgeValueComparator());
//        else if (index == 2) rank.sort(NSGA2Chromosome.connectivityComparator());
//
//        rank.get(0).crowdingDistance = Double.POSITIVE_INFINITY;
//        rank.get(rank.size() - 1).crowdingDistance = Double.POSITIVE_INFINITY;
//        double span = Math.abs(rank.get(0).cost[index] - rank.get(rank.size() - 1).cost[index]);
//        for (int i = 1; i < rank.size() - 1; i++) {
//            rank.get(i).crowdingDistance += Math.abs(rank.get(i-1).cost[index] - rank.get(i+1).cost[index]) / span;
//        }
//    }

}
