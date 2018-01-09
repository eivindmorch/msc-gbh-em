import simulation.SimController;
import simulation.federate.Federate;
import util.SystemMode;
import util.SystemStatus;

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
