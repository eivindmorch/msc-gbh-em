package core;

import core.simulation.SimController;
import core.simulation.federate.Federate;
import core.unit.UnitHandler;
import core.unit.UnitLogger;
import core.util.SystemStatus;
import experiments.experiment1.unit.Experiment1AddUnitMethod;
import experiments.experiment1.unit.Experiment1UnitInfo;

import static core.util.SystemUtil.sleepSeconds;

public class DeterminismTester {

    public DeterminismTester() {
        Experiment1UnitInfo.init();
        UnitHandler.setAddUnitMethod(new Experiment1AddUnitMethod());
        UnitLogger.setIntraResourcesWritingDirectory("data/determinism-testing/" + SystemStatus.START_TIME_STRING + "/");
    }

    private void run() {
        Federate.getInstance().start();

        Federate.getInstance().addTickListener(SimController.getInstance());
        Federate.getInstance().addPhysicalEntityUpdatedListener(SimController.getInstance());

        SimController.getInstance().startSimEngine();
        SimController.getInstance().startSimGui();
        sleepSeconds(10);


        for (int i = 0; i < 10000; i++) {
            SimController.getInstance().loadScenario(
                    "C:\\MAK\\vrforces4.5\\userData\\scenarios\\it3903\\determinism-tests\\" +
                            "makland-det-test-move-to-location-multiple.scnx"
            );
            runSimulationForNTicks(100);
            sleepSeconds(1);
        }
    }

    private void runSimulationForNTicks(int ticks){
        SimController.getInstance().play(ticks);

        synchronized (SimController.getInstance().SIMULATION_RUNNING_LOCK) {
            while(SimController.getInstance().simulationRunning) {
                try {
                    SimController.getInstance().SIMULATION_RUNNING_LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        DeterminismTester determinismTester = new DeterminismTester();
        sleepSeconds(5);
        determinismTester.run();
    }
}
