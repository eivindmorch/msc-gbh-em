package experiments.experiment1;

import core.simulation.Rti;
import core.simulation.SimController;
import core.simulation.federate.Federate;
import core.training.Trainer;
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
        // Setup
        Rti.getInstance().start();

        sleepSeconds(5);
        Federate.getInstance().start();

        Federate.getInstance().addTickListener(SimController.getInstance());
        Federate.getInstance().addPhysicalEntityUpdatedListener(SimController.getInstance());

        sleepSeconds(5);
        SimController.getInstance().startSimEngine();

        sleepSeconds(10);
        SimController.getInstance().startSimGui();

        sleepSeconds(5);
        Trainer<FollowerUnit, FollowerEvaluationDataRow> trainer = new Trainer<>(FollowerUnit.class, FollowerEvaluationDataRow.class);
        trainer.setAlgorithm(new SimpleSingleObjectiveGA<>(FollowerEvaluationDataRow.class));
        trainer.start();


//        sleepSeconds(20);
//        rti.destroy();
    }

    private void testCgfControlInteractions() {
        // Tests
        sleepSeconds(10);
        SimController.getInstance().loadScenario(
                "C:/MAK/vrforces4.5/userData/scenarios/it3903/follow_time-constrained-run-to-complete.scnx"
        );

        sleepSeconds(10);
        SimController.getInstance().play();
//        while (true) {
//            sleepSeconds(10);
//            SimController.getInstance().rewind();
//            sleepSeconds(2);
//            SimController.getInstance().play();
//        }


        sleepSeconds(10);
        SimController.getInstance().pause();

        sleepSeconds(10);
        SimController.getInstance().rewind();

        sleepSeconds(10);
        SimController.getInstance().play();

        sleepSeconds(10);
        SimController.getInstance().pause();

        sleepSeconds(10);
        SimController.getInstance().loadScenario(
                "C:/MAK/vrforces4.5/userData/scenarios/it3903/follow_time-constrained-earth.scnx"
        );
        sleepSeconds(10);
        SimController.getInstance().play();
    }
}
