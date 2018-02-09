package core.training;

import com.badlogic.gdx.ai.btree.Task;
import core.data.DataSet;
import core.data.rows.DataRow;
import core.simulation.SimulationEndedListener;
import core.unit.Unit;
import core.unit.UnitLogger;
import core.util.SystemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.simulation.SimController;
import core.training.algorithms.Algorithm;
import core.unit.ControlledUnit;

import java.util.ArrayList;
import java.util.List;

import static core.SystemSettings.INTRA_RESOURCES_EXAMPLES_FOLDER_PATH;
import static core.util.SystemUtil.sleepMilliseconds;


/**
 *
 * @param <U> unit to be trained. Has to extend {@link Unit}.
 * @param <D> data type to be used for evaluation. Has to extend {@link DataRow}.
 */
public class Trainer<U extends Unit, D extends DataRow> implements SimulationEndedListener{

    private final Logger logger = LoggerFactory.getLogger(Trainer.class);

    private Algorithm<D, ?> algorithm;
    private List<DataSet<D>> exampleDataSets;

    private Class<U> unitToTrainClass;
    private Class<D> evaluationDataRowClass;
    private FitnessEvaluator fitnessEvaluator;

    private int currentEpoch = 0;

    private volatile boolean simulationRunning;
    private final Object SIMULATION_ENDED_LOCK = new Object();

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
            exampleDataSets.add(new DataSet<>(evaluationDataRowClass, INTRA_RESOURCES_EXAMPLES_FOLDER_PATH + exampleName));
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
            logger.info("Simulating chromosome " + chromosomeIndex + ": " + population.get(chromosomeIndex));

            UnitLogger.setIntraResourcesWritingDirectory(getChromosomeFileDirectory(currentEpoch, exampleIndex, chromosomeIndex));

            Task btree = population.get(chromosomeIndex).getBtree();
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
    public void simulatePopulation(Population population, List<DataSet> exampleDataSets) {
        for (int exampleIndex = 0; exampleIndex < exampleDataSets.size(); exampleIndex++) {
            DataSet exampleDataSet = exampleDataSets.get(exampleIndex);
            simulatePopulation(population, exampleDataSet.getNumOfTicks(), exampleIndex, exampleDataSet.getScenarioPath());
        }
    }

    private void runSimulationForNTicks(int ticks){
        simulationRunning = true;
        SimController.getInstance().play(ticks, this);
        synchronized (SIMULATION_ENDED_LOCK) {
            while(simulationRunning) {
                try {
                    SIMULATION_ENDED_LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Class<U> getUnitToTrainClass() {
        return unitToTrainClass;
    }

    @Override
    public void onSimulationEnd() {
        simulationRunning = false;
        synchronized (SIMULATION_ENDED_LOCK) {
            SIMULATION_ENDED_LOCK.notifyAll();
        }
    }

    public Population getPopulation() {
        return algorithm.getPopulation();
    }

    public void setFitness(Population population, int epoch) {
        for (int chromosomeIndex = 0; chromosomeIndex < population.getSize(); chromosomeIndex++) {

            List<DataSet<D>> chromosomeDataSets = new ArrayList<>();

            for (int exampleNumber = 0; exampleNumber <exampleDataSets.size() ; exampleNumber++) {

                DataSet<D> exampleDataSet = exampleDataSets.get(exampleNumber);

                String chromosomeFileDirectory = getChromosomeFileDirectory(epoch, exampleNumber, chromosomeIndex);
                DataSet<D> chromosomeDataSet = new DataSet<>(
                        evaluationDataRowClass,
                        chromosomeFileDirectory
                                + exampleDataSet.getUnitMarking()
                                + "/" + exampleDataSet.getDataSetName()
                                + ".csv"
                );
                chromosomeDataSets.add(chromosomeDataSet);
            }

            Chromosome chromosome = population.get(chromosomeIndex);
            ArrayList<Double> chromosomeFitness = evaluate(chromosome, exampleDataSets, chromosomeDataSets);
            chromosome.setFitness(chromosomeFitness);
        }
    }

    private ArrayList<Double> evaluate(
            Chromosome chromosome,
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

    private static String getChromosomeFileDirectory(int epoch, int example, int chromosome) {
        return "data/training/" +
                SystemStatus.START_TIME_STRING + "/" +
                "epoch" + epoch + "/" +
                "example" + example + "/" +
                "chromosome" + chromosome + "/";
    }

}
