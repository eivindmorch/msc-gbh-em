package experiments.experiment1;

import core.simulation.Rti;
import core.simulation.SimController;
import core.simulation.federate.Federate;
import core.training.Trainer;
import core.training.algorithms.Algorithm;
import core.training.algorithms.SimpleSingleObjectiveGA;
import core.unit.UnitHandler;
import experiments.experiment1.data.rows.FollowerEvaluationDataRow;
import experiments.experiment1.unit.Experiment1AddUnitMethod;
import experiments.experiment1.unit.Experiment1UnitInfo;
import experiments.experiment1.unit.FollowerUnit;

import static core.util.SystemUtil.sleepSeconds;

public class Experiment1 {

    public static void main(String[] args) {
        new Experiment1();
    }

    private Experiment1() {
        Experiment1UnitInfo.init();
        UnitHandler.setAddUnitMethod(new Experiment1AddUnitMethod());
        run();
    }


    private void run() {

        Rti.getInstance().start();

        sleepSeconds(5);
        SimController.getInstance().startSimEngine();
        SimController.getInstance().startSimGui();

        sleepSeconds(5);
        Federate.getInstance().start();

        Federate.getInstance().addTickListener(SimController.getInstance());
        Federate.getInstance().addPhysicalEntityUpdatedListener(SimController.getInstance());

        sleepSeconds(10);


        // TODO Population size as argument?
        Algorithm<FollowerEvaluationDataRow> algorithm = new SimpleSingleObjectiveGA<>(
                FollowerEvaluationDataRow.class,
                new Experiment1FitnessEvaluator()
        );

        String[] exampleFileNames = new String[]{
                "experiment1/0.csv"
        };

        Trainer trainer = new Trainer<>(
                FollowerUnit.class,
                FollowerEvaluationDataRow.class,
                algorithm,
                exampleFileNames
        );

        trainer.train(2);

//        sleepSeconds(20);
//        rti.destroy();
    }
}
