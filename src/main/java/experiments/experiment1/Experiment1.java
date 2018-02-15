package experiments.experiment1;

import core.simulation.SimController;
import core.simulation.federate.Federate;
import core.training.Trainer;
import core.training.algorithms.Algorithm;
import core.training.algorithms.NSGA2.NSGA2;
import core.training.algorithms.NSGA2.NSGA2Chromosome;
import core.unit.UnitHandler;
import experiments.experiment1.data.rows.FollowerEvaluationDataRow;
import experiments.experiment1.unit.Experiment1AddUnitMethod;
import experiments.experiment1.unit.Experiment1UnitInfo;
import experiments.experiment1.unit.FollowerUnit;

import static core.util.SystemUtil.sleepSeconds;

public class Experiment1 {

    public static void main(String[] args) {
        new Experiment1();
    }

    private Experiment1() {
        Experiment1UnitInfo.init();
        UnitHandler.setAddUnitMethod(new Experiment1AddUnitMethod());
        run();
    }

    private void run() {

//        Rti.getInstance().start();

        sleepSeconds(5);
        Federate.getInstance().start();

        Federate.getInstance().addTickListener(SimController.getInstance());
        Federate.getInstance().addPhysicalEntityUpdatedListener(SimController.getInstance());

        SimController.getInstance().startSimEngine();
//        SimController.getInstance().startSimGui();
        sleepSeconds(10);


        // TODO Population size as argument?
        Algorithm<FollowerEvaluationDataRow, NSGA2Chromosome> algorithm = new NSGA2<>(
                10,
                0.5,
                0.8,
                3,
                10
        );

        String[] exampleFileNames = new String[]{
                "experiment1/brooklyn.csv",
                "experiment1/village.csv"
        };

        Trainer trainer = new Trainer<>(
                FollowerUnit.class,
                FollowerEvaluationDataRow.class,
                new Experiment1FitnessEvaluator(2),
                algorithm,
                exampleFileNames
        );


        trainer.train(100000);

//        sleepSeconds(20);
//        rti.destroy();
    }

}
