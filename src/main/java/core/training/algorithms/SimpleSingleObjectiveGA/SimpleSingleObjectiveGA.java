package core.training.algorithms.SimpleSingleObjectiveGA;


import com.badlogic.gdx.ai.btree.Task;
import core.data.DataSet;
import core.data.rows.DataRow;
import core.training.Chromosome;
import core.model.btree.BehaviorTreeUtil;
import core.training.FitnessEvaluator;
import core.training.Population;
import core.training.algorithms.Algorithm;
import core.training.algorithms.NSGA2.NSGA2Chromosome;
import core.util.SystemUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class SimpleSingleObjectiveGA<D extends DataRow> extends Algorithm<D, Chromosome> {

    private OneDimensionalComparator oneDimensionalComparator;
    private Random random;

    public SimpleSingleObjectiveGA(Class<D> evaluationDataRowClass, FitnessEvaluator fitnessEvaluator) {
        super(evaluationDataRowClass, fitnessEvaluator);
    }

    @Override
    public void setup() {
        oneDimensionalComparator = new OneDimensionalComparator();
        random = new Random();
        population = Population.generateRandomPopulation(
                SimpleSingleObjectiveGASettings.populationSize,
                trainer.getUnitToTrainClass(),
                Chromosome.class
        );

        System.out.println(population);
    }

    @Override
    public void step(int epoch, int exampleNumber, DataSet<D> exampleDataSet) {
        // SIMULATION
            trainer.simulatePopulation(population, exampleDataSet.getNumOfTicks(), exampleDataSet.getScenarioPath());

        // EVALUATION

        // DONE Obtain example file
        // Obtain files for simulated population
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
            population.get(chromosomeIndex).setFitness(chromosomeFitness);
        }

            // Evaluate each chromosome by running functions in FitnessFunctions and store in fitness list in the chromosomes

        // SELECTION
            population.sort(oneDimensionalComparator);
            Population newPopulation = new Population();

            // Elitism
            for (int i = 0; i < SimpleSingleObjectiveGASettings.elitismPercentage * population.getSize(); i++) {
                newPopulation.add(population.get(i).clone());
            }

            // Crossover or mutation
            while (newPopulation.getSize() < SimpleSingleObjectiveGASettings.populationSize) {
                Chromosome parent1 = population.selectionTournament(2, oneDimensionalComparator);

                // TODO Separate in methods
                if (random.nextDouble() < SimpleSingleObjectiveGASettings.crossoverRate) {
                    Chromosome parent2 = population.selectionTournament(2, oneDimensionalComparator);
                    Task crossoverTask = BehaviorTreeUtil.crossover(parent1.getBtree(), parent2.getBtree());
                    newPopulation.add(new Chromosome(crossoverTask));
                } else if (random.nextDouble() < SimpleSingleObjectiveGASettings.mutationRate) {
                    Task mutateTask = BehaviorTreeUtil.mutate(parent1.getBtree(), trainer.getUnitToTrainClass());
                    newPopulation.add(new Chromosome(mutateTask));
                } else {
                    newPopulation.add(parent1.clone());
                }
            }
        System.out.println("OLD " + population);
        population = newPopulation;
    }

    private ArrayList<Double> evaluate(DataSet<D> exampleDataSet, DataSet<D> chromosomeDataSet) {
        try {
            return fitnessEvaluator.evaluate(exampleDataSet, chromosomeDataSet);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }


    @Override
    public void cleanup() {

    }

    // TODO Move to Chromosome?
    private class OneDimensionalComparator implements Comparator<Chromosome> {
        @Override
        public int compare(Chromosome o1, Chromosome o2) {
            if (o1.getFitness().get(0) < o2.getFitness().get(0)) {
                return -1;
            } else if (o1.getFitness().get(0) > o2.getFitness().get(0)) {
                return 1;
            }
            return 0;
        }
    }
}
