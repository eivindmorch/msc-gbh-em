package experiments.experiment1;

import core.model.btree.GenBehaviorTree;
import core.model.btree.task.unit.Wait;
import core.simulation.Rti;
import core.simulation.SimController;
import core.simulation.federate.Federate;
import core.unit.ControlledUnit;
import core.unit.Unit;
import core.unit.UnitHandler;
import core.unit.UnitTypeInfo;
import experiments.experiment1.model.btree.task.unit.followerunit.IsApproaching;
import experiments.experiment1.model.btree.task.unit.followerunit.IsCloseEnough;
import experiments.experiment1.model.btree.task.unit.followerunit.Move;
import experiments.experiment1.model.btree.task.unit.followerunit.TurnToHeading;
import experiments.experiment1.unit.AddUnitMethod;
import experiments.experiment1.unit.Experiment1Unit;
import experiments.experiment1.unit.FollowerUnit;

import java.util.ArrayList;
import java.util.Arrays;

import static core.util.SystemUtil.sleepSeconds;

public class Experiment1 {

    public static void main(String[] args) {
        new Experiment1();
    }

    private Experiment1() {

        UnitTypeInfo.add(
                "Follower", "F", FollowerUnit.class,
                Arrays.asList(
                        Move.class,
                        Wait.class,
                        IsApproaching.class,
                        IsCloseEnough.class,
                        TurnToHeading.class
                )
        );
        UnitTypeInfo.add(
                "Wanderer", "W", Experiment1Unit.class, new ArrayList<>()
        );

        UnitHandler.setAddUnitMethod(new AddUnitMethod());
        run();
    }

    private void run() {
        // Setup
        Rti.getInstance().start();

        sleepSeconds(5);
        Federate.getInstance().start();

        ControlledUnit.setControlledUnitBtreeMap(FollowerUnit.class, GenBehaviorTree.generateTestTree());

        Federate.getInstance().addTickListener(SimController.getInstance());
        Federate.getInstance().addPhysicalEntityUpdatedListener(SimController.getInstance());

        sleepSeconds(5);
        SimController.getInstance().startSimEngine();

        sleepSeconds(10);
        SimController.getInstance().startSimGui();


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



//        sleepSeconds(20);
//        rti.destroy();
    }
}
