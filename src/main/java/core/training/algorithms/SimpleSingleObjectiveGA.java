package core.training.algorithms;


import core.data.DataSet;
import core.data.rows.DataRow;
import core.model.btree.EvaluatedGenBehaviorTree;
import core.settings.algorithms.SimpleSingleObjectiveGASettings;
import core.training.Population;
import core.util.Reader;
import core.util.SystemUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class SimpleSingleObjectiveGA<D extends DataRow> extends Algorithm<D> {

    OneDimensionalComparator oneDimensionalComparator;
    Random random;

    public SimpleSingleObjectiveGA(Class<D> evaluationDataRowClass) {
        super(evaluationDataRowClass);
    }

    @Override
    public void setup() {
        oneDimensionalComparator = new OneDimensionalComparator();
        random = new Random();
        population = new Population();
        population.generateRandomPopulation(SimpleSingleObjectiveGASettings.populationSize);
    }

    @Override
    public void step(int epoch, int exampleNumber, DataSet<D> exampleDataSet) {
        // SIMULATION
            trainer.simulatePopulation(population);

        // EVALUATION

        // DONE Obtain example file
        // Obtain files for simulated population
        for (int chromosomeIndex = 0; chromosomeIndex < population.getSize(); chromosomeIndex++) {
            String intraResourcesScenarioLogsPath = SystemUtil.getDataFileIntraResourcesFolderPath(epoch, exampleNumber, chromosomeIndex);
            Reader reader = new Reader(
                    intraResourcesScenarioLogsPath
                    + exampleDataSet.getUnitMarking()
                    + "/" + exampleDataSet.getDataSetName()
                    + ".csv"
            );

            reader.readLine(); // Ignore timestamp metadata
            reader.readLine(); // Ignore scenario metadata
            reader.readLine(); // Ignore unit marking
            reader.readLine(); // Ignore header

            String line;
            for (int i = 0; i < exampleDataSet.getNumOfTicks() && (line = reader.readLine()) != null; i++) {
                D exampleEvaluationDataRow = exampleDataSet.getDataRows().get(i);
                try {
                    D chromosomeEvaluationDataRow = evaluationDataRowClass.newInstance();
                    chromosomeEvaluationDataRow.setValues(Reader.stringToCsvList(line));
                    ArrayList<Double> fitnessList = evaluate(exampleEvaluationDataRow, chromosomeEvaluationDataRow);
                    population.get(chromosomeIndex).setFitness(fitnessList);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }

            // Evaluate each chromosome by running functions in FitnessFunctions and store in fitness list in the chromosomes

        // SELECTION
            population.sort(oneDimensionalComparator);
            Population newPopulation = new Population();

            // Elitism
            for (int i = 0; i < SimpleSingleObjectiveGASettings.elitismPercentage * population.getSize(); i++) {
                newPopulation.add(population.get(i));
            }
            // TODO Replace random with binary tournament
            // Crossover or mutation
            while (newPopulation.getSize() > SimpleSingleObjectiveGASettings.populationSize) {
                EvaluatedGenBehaviorTree parent1 = population.selectionTournament(2, oneDimensionalComparator);

                if (random.nextDouble() < SimpleSingleObjectiveGASettings.crossoverRate) {
                    EvaluatedGenBehaviorTree parent2 = population.selectionTournament(2, oneDimensionalComparator);
                    newPopulation.add(population.crossover(parent1, parent2));
                } else if (random.nextDouble() < SimpleSingleObjectiveGASettings.mutationRate) {
                    newPopulation.add(population.mutate(parent1));
                } else {
                    newPopulation.add(parent1.clone());
                }
            }
        population = newPopulation;
    }

    private ArrayList<Double> evaluate(D exampleEvaluationDataRow, D chromosomeEvaluationDataRow) {
//        System.out.println();
//        System.out.println(exampleEvaluationDataRow.toString());
//        System.out.println(chromosomeEvaluationDataRow.toString());
        // TODO
        return new ArrayList<>();
    }


    @Override
    public void cleanup() {

    }

    private class OneDimensionalComparator implements Comparator<EvaluatedGenBehaviorTree> {
        @Override
        public int compare(EvaluatedGenBehaviorTree o1, EvaluatedGenBehaviorTree o2) {
            if (o1.getFitness().get(0) < o2.getFitness().get(0)) {
                return 1;
            } else if (o1.getFitness().get(0) > o2.getFitness().get(0)) {
                return -1;
            }
            return 0;
        }
    }
}
