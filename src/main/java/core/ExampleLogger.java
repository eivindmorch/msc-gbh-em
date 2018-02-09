package core;

import core.model.btree.BehaviorTreeUtil;
import core.simulation.SimController;
import core.simulation.federate.Federate;
import core.unit.ControlledUnit;
import core.unit.UnitHandler;
import core.unit.UnitLogger;
import core.util.SystemStatus;
import experiments.experiment1.unit.Experiment1AddUnitMethod;
import experiments.experiment1.unit.Experiment1UnitInfo;
import experiments.experiment1.unit.FollowerUnit;

import static core.util.SystemUtil.sleepSeconds;

public class ExampleLogger {

    public ExampleLogger() {
        Federate.getInstance().start();

        Federate.getInstance().addTickListener(SimController.getInstance());
        Federate.getInstance().addPhysicalEntityUpdatedListener(SimController.getInstance());

        Experiment1UnitInfo.init();
        UnitHandler.setAddUnitMethod(new Experiment1AddUnitMethod());

        ControlledUnit.setControlledUnitBtreeMap(FollowerUnit.class, BehaviorTreeUtil.generateTestTree());

        UnitLogger.setIntraResourcesWritingDirectory("example_logging/" + SystemStatus.START_TIME_STRING);
    }

    public void run() {
        SimController.getInstance().play();
    }

    public static void main(String[] args) {
        ExampleLogger exampleLogger = new ExampleLogger();
        sleepSeconds(5);
        exampleLogger.run();
    }
}
