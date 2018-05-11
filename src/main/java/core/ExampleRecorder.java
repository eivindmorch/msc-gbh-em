package core;

import core.model.btree.BehaviorTreeUtil;
import core.simulation.SimController;
import core.simulation.hla.HlaManager;
import core.unit.ControlledUnit;
import core.unit.UnitHandler;
import core.unit.UnitLogger;
import core.util.SystemStatus;
import experiments.experiment1.unit.Experiment1AddUnitMethod;
import experiments.experiment1.unit.Experiment1UnitInfo;
import experiments.experiment1.unit.FollowerUnit;

public class ExampleRecorder {

    private ExampleRecorder() {
        Experiment1UnitInfo.init();
        UnitHandler.setAddUnitMethod(new Experiment1AddUnitMethod());
        UnitLogger.setIntraResourcesWritingDirectory("data/example_logging/" + SystemStatus.START_TIME_STRING + "/");
        ControlledUnit.setControlledUnitBtreeMap(FollowerUnit.class, BehaviorTreeUtil.generateTestTree());
    }

    public void run() {
        HlaManager.getInstance().start();

        HlaManager.getInstance().addTickListener(SimController.getInstance());
        HlaManager.getInstance().addPhysicalEntityUpdatedListener(SimController.getInstance());

        SimController.getInstance().play();
    }

    public static void main(String[] args) {
        ExampleRecorder exampleRecorder = new ExampleRecorder();
        exampleRecorder.run();
    }
}
