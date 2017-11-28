import simulation.SimController;
import training.Trainer;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void run() {
        SimController simController = new SimController();
        simController.init();
        simController.initSimEngine();

        try {
            TimeUnit.SECONDS.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Resetting SimEngine");
        simController.reset();

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
