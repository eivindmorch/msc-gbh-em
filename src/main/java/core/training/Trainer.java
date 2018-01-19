package core.training;

import core.data.DataSet;
import core.data.rows.DataRow;
import core.simulation.SimulationEndedListener;
import core.unit.Unit;
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

    private volatile boolean simulationRunning;

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
        for (int epoch = 0; epoch < epochs; epoch++) {
            // TODO Not working when calling train twice (overwrites)
            SystemStatus.currentTrainingEpoch = epoch;
            for (int exampleDataSetIndex = 0; exampleDataSetIndex < exampleDataSets.size(); exampleDataSetIndex++) {
                SystemStatus.currentTrainingExampleDataSetIndex = exampleDataSetIndex;
                DataSet<D> exampleDataSet = exampleDataSets.get(exampleDataSetIndex);
                SimController.getInstance().loadScenario(exampleDataSet.getScenarioPath());
                // TODO
                sleepSeconds(10);
                algorithm.step(epoch, exampleDataSetIndex, exampleDataSet);
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
    public void simulatePopulation(Population population, int numOfTicks) {
        for (int i = 0; i < population.getSize(); i++) {
            SimController.getInstance().rewind();
            SystemStatus.currentTrainingChromosome = i;
            ControlledUnit.setControlledUnitBtreeMap(unitToTrainClass, population.get(i).getBtree());
            runSimulationForNTicks(numOfTicks);
        }
    }

    private void runSimulationForNTicks(int ticks){
        simulationRunning = true;
        SimController.getInstance().play(ticks, this);
        while(simulationRunning) {
        }
    }

    public Class<U> getUnitToTrainClass() {
        return unitToTrainClass;
    }

    @Override
    public void onSimulationEnd() {
        simulationRunning = false;
    }
}
