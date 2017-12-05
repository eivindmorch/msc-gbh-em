package training;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.TrainingSettings;
import simulation.SimController;
import simulation.federate.Federate;
import training.algorithms.Algorithm;


public class Trainer {

    private final Logger logger = LoggerFactory.getLogger(Trainer.class);

    private Algorithm algorithm;
    private boolean running;

    public Trainer() {
        try {
            algorithm = TrainingSettings.algorithm.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Federate federate = Federate.getInstance();
        federate.init();

        SimController simController = new SimController();

        federate.addTickListener(simController);
        federate.addPhysicalEntityUpdatedListener(simController);

//        simController.startSimEngine();
    }

    public void run() {
        algorithm.setup();
        while (running) {
//             simController.simulate()
            algorithm.epoch();
        }
        // ParetoPlotter.plot();
    }

    public static void main(String[] args) {
        Trainer trainer = new Trainer();
    }




}
