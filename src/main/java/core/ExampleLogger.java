package core;

import core.simulation.SimController;
import core.simulation.federate.Federate;
import core.util.SystemMode;
import core.util.SystemStatus;

public class ExampleLogger {

    SimController simController;

    public ExampleLogger() {
        SystemStatus.systemMode = SystemMode.EXAMPLE_LOGGING;

        Federate.getInstance().start();

        Federate.getInstance().addTickListener(SimController.getInstance());
        Federate.getInstance().addPhysicalEntityUpdatedListener(SimController.getInstance());
    }

    public void run() {
        simController.play();
    }

    public static void main(String[] args) {
        ExampleLogger exampleLogger = new ExampleLogger();
    }
}
