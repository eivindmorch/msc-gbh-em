import simulation.SimController;
import training.Trainer;

public class Main {

    public static void run() {
        SimController simController = new SimController();
        simController.init();
//        simController.initSimEngine();


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
