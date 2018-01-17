package core.training;

import core.data.DataSet;
import core.data.rows.DataRow;
import core.unit.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.settings.TrainingSettings;
import core.simulation.SimController;
import core.training.algorithms.Algorithm;
import core.unit.ControlledUnit;
import core.util.SystemMode;
import core.util.SystemStatus;

import java.util.ArrayList;
import java.util.List;

import static core.util.SystemUtil.sleepSeconds;


// U for unit to be trained, D for data to be used for evaluation
public class Trainer<U extends Unit, D extends DataRow> {

    private final Logger logger = LoggerFactory.getLogger(Trainer.class);

    private Algorithm<D> algorithm;
    private boolean running;
    private List<DataSet<D>> exampleDataSets;

    private Class<U> unitToTrainClass;
    private Class<D> evaluationDataRowClass;

    public Trainer(Class<U> unitToTrainClass, Class<D> evaluationDataRowClass) {
        SystemStatus.systemMode = SystemMode.TRAINING;

        this.unitToTrainClass = unitToTrainClass;
        this.evaluationDataRowClass = evaluationDataRowClass;

        exampleDataSets = loadExampleDataSets();
    }

    public void start() {
        running = true;
        algorithm.setup();
        while (running) {
            for (int epoch = 0; epoch < TrainingSettings.epochs; epoch++) {
                SystemStatus.currentTrainingEpoch = epoch;
                for (int exampleDataSetIndex = 0; exampleDataSetIndex < exampleDataSets.size(); exampleDataSetIndex++) {
                    SystemStatus.currentTrainingExampleDataSetIndex = exampleDataSetIndex;
                    DataSet<D> exampleDataSet = exampleDataSets.get(exampleDataSetIndex);
                    SimController.getInstance().loadScenario(exampleDataSet.getScenarioPath());
                    algorithm.step(epoch, exampleDataSetIndex, exampleDataSet);
                }
            }
        }
        // ParetoPlotter.plot();
    }

    private ArrayList<DataSet<D>> loadExampleDataSets() {
        ArrayList<DataSet<D>> exampleDataSets = new ArrayList<>();
        for (String exampleName : TrainingSettings.examples) {
            exampleDataSets.add(new DataSet<>(evaluationDataRowClass, exampleName));
        }
        return exampleDataSets;
    }

    public void setAlgorithm(Algorithm<D> algorithm) {
        this.algorithm = algorithm;
        this.algorithm.setTrainer(this);
    }

    public void simulatePopulation(Population population) {
        for (int i = 0; i < population.getSize(); i++) {
            SystemStatus.currentTrainingChromosome = i;
            ControlledUnit.setControlledUnitBtreeMap(unitToTrainClass, population.get(i).getBtree());
            SimController.getInstance().play();
            sleepSeconds(10);
            SimController.getInstance().pause();
            // TODO Enable
//            SimController.getInstance().rewind();
        }
    }

}
