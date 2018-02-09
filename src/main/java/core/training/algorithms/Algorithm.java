package core.training.algorithms;

import core.data.DataSet;
import core.data.rows.DataRow;
import core.training.Chromosome;
import core.training.FitnessEvaluator;
import core.training.Population;
import core.training.Trainer;
import core.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;


public abstract class Algorithm<D extends DataRow, C extends Chromosome> {

    protected Trainer trainer;
    protected Population<C> population;
    protected FitnessEvaluator fitnessEvaluator;
    public Class<D> evaluationDataRowClass;

    public Algorithm(Class<D> evaluationDataRowClass, FitnessEvaluator fitnessEvaluator) {
        this.population = new Population<>();
        this.fitnessEvaluator = fitnessEvaluator;
        this.evaluationDataRowClass = evaluationDataRowClass;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public Population<C> getPopulation() {
        return population;
    }

    public abstract void setup();

    @SuppressWarnings("unchecked")
    public abstract void step(int epoch, List<DataSet<D>> exampleDataSets);

    public abstract void cleanup();

    protected void setFitness(Population<C> population, int epoch, List<DataSet<D>> exampleDataSets) {
        for (int chromosomeIndex = 0; chromosomeIndex < population.getSize(); chromosomeIndex++) {

            List<DataSet<D>> chromosomeDataSets = new ArrayList<>();

            for (int exampleNumber = 0; exampleNumber <exampleDataSets.size() ; exampleNumber++) {

                DataSet<D> exampleDataSet = exampleDataSets.get(exampleNumber);

                String intraResourcesScenarioLogsPath = SystemUtil.getDataFileIntraResourcesFolderPath(epoch, exampleNumber, chromosomeIndex);
                DataSet<D> chromosomeDataSet = new DataSet<>(
                        evaluationDataRowClass,
                        intraResourcesScenarioLogsPath
                                + exampleDataSet.getUnitMarking()
                                + "/" + exampleDataSet.getDataSetName()
                                + ".csv"
                );
                chromosomeDataSets.add(chromosomeDataSet);
            }

            C chromosome = population.get(chromosomeIndex);
            ArrayList<Double> chromosomeFitness = evaluate(chromosome, exampleDataSets, chromosomeDataSets);
            chromosome.setFitness(chromosomeFitness);
        }
    }

    private ArrayList<Double> evaluate(
            C chromosome,
            List<DataSet<D>> exampleDataSets,
            List<DataSet<D>> chromosomeDataSets
    ) {
        try {
            return fitnessEvaluator.evaluate(chromosome, exampleDataSets, chromosomeDataSets);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
