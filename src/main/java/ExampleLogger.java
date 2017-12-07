import simulation.SimController;
import simulation.federate.Federate;
import util.SystemMode;
import util.SystemStatus;

public class ExampleLogger {

    SimController simController;

    public ExampleLogger() {
        SystemStatus.systemMode = SystemMode.EXAMPLE_LOGGING;

        Federate federate = Federate.getInstance();
        federate.init();

        simController = new SimController();

        federate.addTickListener(simController);
        federate.addPhysicalEntityUpdatedListener(simController);

//        simController.startSimEngine();
    }

    public void run() {
        simController.startResume();
    }

    public static void main(String[] args) {
        ExampleLogger exampleLogger = new ExampleLogger();
    }
}
