package core.training;

import core.BtreeAlt.TempTask;
import core.data.DataSet;
import core.data.rows.DataRow;
import core.unit.Unit;
import core.unit.UnitLogger;
import core.util.SystemStatus;
import core.util.plotting.Plotter;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.simulation.SimController;
import core.training.algorithms.Algorithm;
import core.unit.ControlledUnit;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static core.SystemSettings.INTRA_RESOURCES_EXAMPLES_FOLDER_PATH;
import static core.training.Chromosome.singleObjectiveComparator;
import static core.util.SystemUtil.sleepMilliseconds;


/**
 *
 * @param <U> unit to be trained. Has to extend {@link Unit}.
 * @param <D> data type to be used for evaluation. Has to extend {@link DataRow}.
 */
public class Trainer<U extends Unit, D extends DataRow> {

    private final Logger logger = LoggerFactory.getLogger(Trainer.class);

    private Algorithm<D, ?> algorithm;
    private List<DataSet<D>> exampleDataSets;

    private Class<U> unitToTrainClass;
    private Class<D> evaluationDataRowClass;
    private FitnessEvaluator fitnessEvaluator;

    private int currentEpoch = 0;

    public LinkedHashMap<String, XYSeriesCollection> fitnessHistoryCollections;

    public Trainer(Class<U> unitToTrainClass, Class<D> evaluationDataRowClass, FitnessEvaluator fitnessEvaluator, Algorithm<D, ?> algorithm, String[] exampleFileNames) {
        this.unitToTrainClass = unitToTrainClass;
        this.evaluationDataRowClass = evaluationDataRowClass;
        this.fitnessEvaluator = fitnessEvaluator;

        this.algorithm = algorithm;
        this.algorithm.setTrainer(this);
        this.algorithm.setup();

        exampleDataSets = loadExampleDataSets(exampleFileNames);
    }

    public void train(int epochs) {
        for (;currentEpoch < currentEpoch + epochs; currentEpoch++) {
            logger.info("================ EPOCH " + currentEpoch  + " ================");
            // TODO Not working when calling train twice (overwrites)
            algorithm.step(currentEpoch, exampleDataSets);
        }
    }

    private ArrayList<DataSet<D>> loadExampleDataSets(String[] exampleFileNames) {
        ArrayList<DataSet<D>> exampleDataSets = new ArrayList<>();
        for (String exampleName : exampleFileNames) {
            try {
                exampleDataSets.add(new DataSet<>(evaluationDataRowClass, INTRA_RESOURCES_EXAMPLES_FOLDER_PATH + exampleName));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return exampleDataSets;
    }

    /**
     * Simulates the population by simulating each individual chromosome for the specified number of ticks.
     * @param population instance of {@link Population} to be simulated
     * @param numOfTicks number of ticks to simulate each chromosome
     * @param scenarioPath the path of the scenario to simulate
     */
    public void simulatePopulation(Population population, int numOfTicks, int exampleIndex, String scenarioPath) {
        logger.info("Simulating population.");
        for (int chromosomeIndex = 0; chromosomeIndex < population.getSize(); chromosomeIndex++) {

            Chromosome chromosome = population.get(chromosomeIndex);
            logger.info("Simulating chromosome " + chromosomeIndex + ": " + chromosome);

            UnitLogger.setIntraResourcesWritingDirectory(getChromosomeFileDirectory(currentEpoch, exampleIndex, chromosome));

            TempTask btree = population.get(chromosomeIndex).getBtree();
            ControlledUnit.setControlledUnitBtreeMap(unitToTrainClass, btree);

            SimController.getInstance().loadScenario(scenarioPath);
            runSimulationForNTicks(numOfTicks);

            sleepMilliseconds(250);
        }
    }

    /**
     * Simulates the population for each {@link DataSet} in {@code exampleDataSets} by simulating each individual
     * chromosome for the specified number of ticks.
     * @param population instance of {@link Population} to be simulated
     * @param exampleDataSets the example {@link DataSet}s to simulate
     */
    public void simulatePopulation(Population population, List<DataSet<D>> exampleDataSets) {
        for (int exampleIndex = 0; exampleIndex < exampleDataSets.size(); exampleIndex++) {
            DataSet exampleDataSet = exampleDataSets.get(exampleIndex);
            simulatePopulation(population, exampleDataSet.getNumOfTicks(), exampleIndex, exampleDataSet.getScenarioPath());
        }
    }

    private void runSimulationForNTicks(int ticks){
        SimController.getInstance().play(ticks);

        synchronized (SimController.getInstance().SIMULATION_RUNNING_LOCK) {
            while(SimController.getInstance().simulationRunning) {
                try {
                    SimController.getInstance().SIMULATION_RUNNING_LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Class<U> getUnitToTrainClass() {
        return unitToTrainClass;
    }

    public Population getPopulation() {
        return algorithm.getPopulation();
    }

    public void setFitness(Population population, int epoch) {
        for (int chromosomeIndex = 0; chromosomeIndex < population.getSize(); chromosomeIndex++) {
            Chromosome chromosome = population.get(chromosomeIndex);

            List<DataSet<D>> chromosomeDataSets = new ArrayList<>();

            for (int exampleNumber = 0; exampleNumber < exampleDataSets.size() ; exampleNumber++) {

                DataSet<D> exampleDataSet = exampleDataSets.get(exampleNumber);

                String chromosomeFileDirectory = getChromosomeFileDirectory(epoch, exampleNumber, chromosome);
                DataSet<D> chromosomeDataSet;
                // TODO Fix cause of missing files -- federate does not get unit data after they have been discovered
                try {
                    chromosomeDataSet = new DataSet<>(
                            evaluationDataRowClass,
                            chromosomeFileDirectory
                                    + exampleDataSet.getUnitMarking()
                                    + "/" + exampleDataSet.getDataSetName()
                                    + ".csv"
                    );
                    chromosomeDataSets.add(chromosomeDataSet);
                } catch (FileNotFoundException e) {
                    logger.warn("Could not find " + chromosomeFileDirectory + " -> skipping chromosome.");
                }
            }

            try {
                LinkedHashMap<String, Double> chromosomeFitness = evaluate(chromosome, exampleDataSets, chromosomeDataSets);
                chromosome.setFitness(chromosomeFitness);
            } catch (Exception e) {
                logger.warn("Could not calculate chromosome fitness -> removing chromosome.", e);
                population.remove(chromosomeIndex);
                chromosomeIndex--;
            }
        }
    }

    private LinkedHashMap<String, Double> evaluate(
            Chromosome chromosome,
            List<DataSet<D>> exampleDataSets,
            List<DataSet<D>> chromosomeDataSets
    ) throws Exception {
            return fitnessEvaluator.evaluate(chromosome, exampleDataSets, chromosomeDataSets);
    }

    private static String getChromosomeFileDirectory(int epoch, int example, Chromosome chromosome) {
        return "data/training/" +
                SystemStatus.START_TIME_STRING + "/" +
                "epoch" + epoch + "/" +
                "example" + example + "/" +
                "chromosome-" + Integer.toHexString(chromosome.hashCode()) + "/";
    }

    public void updateFitnessHistory(Population<? extends Chromosome> population) {
        Population<? extends Chromosome> populationClone = new Population<>(population);

        ArrayList<String> fitnessKeys = new ArrayList<>(populationClone.getChromosomes().get(0).getFitness().keySet());

        if (fitnessHistoryCollections == null) {
            fitnessHistoryCollections = new LinkedHashMap<>();
            for (String fitnessKey : fitnessKeys) {
                XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
                xySeriesCollection.addSeries(new XYSeries("Worst", false, true));
                xySeriesCollection.addSeries(new XYSeries("Average", false, true));
                xySeriesCollection.addSeries(new XYSeries("Best", false, true));
                fitnessHistoryCollections.put(fitnessKey, xySeriesCollection);
            }
        }

        for (String fitnessKey : fitnessKeys) {
            populationClone.sort(singleObjectiveComparator(fitnessKey));

            double worstFitness = populationClone.get(populationClone.getSize() - 1).getFitness().get(fitnessKey);
            fitnessHistoryCollections.get(fitnessKey).getSeries("Worst").add(currentEpoch, worstFitness);

            double averageFitness = 0;
            for (Chromosome chromosome : population.getChromosomes()) {
                averageFitness += chromosome.getFitness().get(fitnessKey);
            }
            averageFitness /= population.getSize();
            fitnessHistoryCollections.get(fitnessKey).getSeries("Average").add(currentEpoch, averageFitness);

            double bestFitness = populationClone.get(0).getFitness().get(fitnessKey);
            fitnessHistoryCollections.get(fitnessKey).getSeries("Best").add(currentEpoch, bestFitness);

        }
    }

    public <C extends Chromosome> ChartPanel getParetoPlot(
            ArrayList<ArrayList<C>> rankedPopulation,
            String fitnessKey1,
            String fitnessKey2
    ) {
        XYSeriesCollection paretoSeriesCollection = new XYSeriesCollection();

        for (int i = 0; i < rankedPopulation.size(); i++) {
            XYSeries paretoSeries = new XYSeries("Rank " + i);

            for (Chromosome chromosome : rankedPopulation.get(i)) {
                paretoSeries.add(chromosome.getFitness().get(fitnessKey1), chromosome.getFitness().get(fitnessKey2));
            }
            paretoSeriesCollection.addSeries(paretoSeries);
        }
        return Plotter.getPlot("Pareto", paretoSeriesCollection, fitnessKey1, fitnessKey2, false);
    }

}
