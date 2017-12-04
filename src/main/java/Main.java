import simulation.SimController;
import simulation.federate.Federate;
import training.Trainer;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void run() {
        Federate federate = new Federate();
        federate.init();

        SimController simController = new SimController();

        federate.addTickListener(simController);
        federate.addPhysicalEntityUpdatedListener(simController);

        simController.initSimEngine();

//        try {
//            TimeUnit.SECONDS.sleep(3);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        simController.startScenario();
//
//        try {
//            TimeUnit.SECONDS.sleep(3);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        simController.stopScenario();
//
//        try {
//            TimeUnit.SECONDS.sleep(3);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        simController.stopScenario();
//
//        try {
//            TimeUnit.SECONDS.sleep(3);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        simController.startScenario();



//        Trainer trainer = new Trainer();
//        trainer.init(); // Generated initial population

        while (true) {
            // Simulate with population btrees
                // Start/restart SimEngine with Scenario

            // Evaluate population
//            Trainer.evaluatePopulation();

            // Select new population
//            Trainer.selectNewPopulation();
        }

    }

    public static void main(String[] args) {
        Main.run();
    }
}
