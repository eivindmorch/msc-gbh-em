import simulation.SimController;
import simulation.federate.Federate;

public class ExampleLogger {

    SimController simController;

    public ExampleLogger() {
        Federate federate = Federate.getInstance();
        federate.init();

        simController = new SimController();

        federate.addTickListener(simController);
        federate.addPhysicalEntityUpdatedListener(simController);

        simController.startSimEngine();
    }

    public void run() {
        simController.resume();
    }
}
