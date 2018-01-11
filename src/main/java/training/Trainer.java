package training;

import data.ExampleDataSet;
import data.rows.FollowerEvaluationDataRow;
import model.btree.GenBehaviorTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.TrainingSettings;
import simulation.Rti;
import simulation.SimController;
import simulation.federate.Federate;
import training.algorithms.Algorithm;
import unit.ControlledUnit;
import unit.FollowerUnit;
import util.SystemMode;
import util.SystemStatus;


import static util.SystemUtil.sleepSeconds;


public class Trainer {

    private final Logger logger = LoggerFactory.getLogger(Trainer.class);

    private Algorithm algorithm;
    private boolean running;
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
