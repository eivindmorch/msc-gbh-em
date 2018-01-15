package core.training;

import core.data.ExampleDataSet;
import experiments.experiment1.Experiment1UnitInfo;
import experiments.experiment1.data.rows.FollowerEvaluationDataRow;
import core.model.btree.GenBehaviorTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.settings.TrainingSettings;
import core.simulation.Rti;
import core.simulation.SimController;
import core.simulation.federate.Federate;
import core.training.algorithms.Algorithm;
import core.unit.ControlledUnit;
import experiments.experiment1.unit.FollowerUnit;
import core.util.SystemMode;
import core.util.SystemStatus;


import static core.util.SystemUtil.sleepSeconds;

// TODO Interface for UnitInfo to use when initiating Trainer
public class Trainer {

    private final Logger logger = LoggerFactory.getLogger(Trainer.class);

    private Algorithm algorithm;
    private boolean running;
    // TODO Move to experiment-dependent package
    private ExampleDataSet<FollowerEvaluationDataRow> test;// TODO Where should comparing be done?

    public Trainer() {
        SystemStatus.systemMode = SystemMode.TRAINING;
        try {
            algorithm = TrainingSettings.algorithm.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        algorithm.setTrainer(this);
        algorithm.setPopulation(new Population());

        Rti.getInstance().start();

        sleepSeconds(5);
        Federate.getInstance().start();

        ControlledUnit.setControlledUnitBtreeMap(FollowerUnit.class, GenBehaviorTree.generateTestTree());

        Federate.getInstance().addTickListener(SimController.getInstance());
        Federate.getInstance().addPhysicalEntityUpdatedListener(SimController.getInstance());

//        simController.startSimEngine();
    }

    public void run() {
        SimController.getInstance().rewind();
        algorithm.setup();
        while (running) {
            for (int i = 0; i < TrainingSettings.epochs; i++) {

            }
        }
        // ParetoPlotter.plot();
    }

    private void loadExamples() {
        for (String exampleName : TrainingSettings.examples) {

        }
    }

    public void simulatePopulation(Population population) {
        for (int i = 0; i < population.getSize(); i++) {
            // TODO
//            setControlledUnitBtreeMap(population.get(i));
            SimController.getInstance().play();
            SimController.getInstance().rewind();
        }
    }

    public static void main(String[] args) {
        Trainer trainer = new Trainer();
    }

}
