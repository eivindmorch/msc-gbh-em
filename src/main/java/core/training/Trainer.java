package core.training;

import com.badlogic.gdx.ai.btree.Task;
import core.data.DataSet;
import core.data.rows.DataRow;
import core.simulation.SimulationEndedListener;
import core.unit.Unit;
import core.util.Grapher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.simulation.SimController;
import core.training.algorithms.Algorithm;
import core.unit.ControlledUnit;
import core.util.SystemMode;
import core.util.SystemStatus;

import java.util.ArrayList;
import java.util.List;

import static core.settings.SystemSettings.INTRA_RESOURCES_EXAMPLES_FOLDER_PATH;
import static core.util.SystemUtil.sleepSeconds;


// U for unit to be trained, D for data to be used for evaluation
public class Trainer<U extends Unit, D extends DataRow> implements SimulationEndedListener{

    private final Logger logger = LoggerFactory.getLogger(Trainer.class);

    private Algorithm<D> algorithm;
    private List<DataSet<D>> exampleDataSets;

    private Class<U> unitToTrainClass;
    private Class<D> evaluationDataRowClass;

    private int currentEpoch = 0;

    private volatile boolean simulationRunning;
    private final Object SIMULATION_ENDED_LOCK = new Object();

    public Trainer(Class<U> unitToTrainClass, Class<D> evaluationDataRowClass, Algorithm<D> algorithm, String[] exampleFileNames) {
        SystemStatus.systemMode = SystemMode.TRAINING;

        this.unitToTrainClass = unitToTrainClass;
        this.evaluationDataRowClass = evaluationDataRowClass;

        this.algorithm = algorithm;
        this.algorithm.setTrainer(this);
        this.algorithm.setup();

        exampleDataSets = loadExampleDataSets(exampleFileNames);
    }

    public void train(int epochs) {
        for (;currentEpoch < currentEpoch + epochs; currentEpoch++) {
            logger.info("EPOCH " + currentEpoch);
            // TODO Not working when calling train twice (overwrites)
            SystemStatus.currentTrainingEpoch = currentEpoch;
            for (int exampleDataSetIndex = 0; exampleDataSetIndex < exampleDataSets.size(); exampleDataSetIndex++) {
                SystemStatus.currentTrainingExampleDataSetIndex = exampleDataSetIndex;
                DataSet<D> exampleDataSet = exampleDataSets.get(exampleDataSetIndex);

                logger.info("Example number " + exampleDataSetIndex + ": " + exampleDataSet);
                algorithm.step(currentEpoch, exampleDataSetIndex, exampleDataSet);
            }
        }
        // ParetoPlotter.plot();
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
     * @param population
     * @param numOfTicks Number of ticks to simulate each chromosome.
     */
    public void simulatePopulation(Population population, int numOfTicks, String scenarioPath) {
        logger.info("Simulating population.");
        for (int i = 0; i < population.getSize(); i++) {
            logger.info("Simulating chromosome " + i + ": " + population.get(i));
            SystemStatus.currentTrainingChromosome = i;

            Task btree = population.get(i).getBtree();
            ControlledUnit.setControlledUnitBtreeMap(unitToTrainClass, btree);

            Grapher.graph(btree);

            if (!scenarioPath.equals(SystemStatus.currentScenario)) {
                // TODO Let UnitLogger write before writers are reset
//                sleepSeconds(5);
                SimController.getInstance().loadScenario(scenarioPath);
                sleepSeconds(5);
            } else {
                SimController.getInstance().rewind();
            }
            runSimulationForNTicks(numOfTicks);

            Grapher.closeGraph(btree);
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
}
