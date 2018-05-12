package experiments.experiment1;

import core.simulation.SimController;
import core.simulation.hla.HlaManager;
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

    private Experiment1() {
        Experiment1UnitInfo.init();
        UnitHandler.setAddUnitMethod(new Experiment1AddUnitMethod());
    }

    private void run() {
//        HlaManager.getInstance().startRti();

        HlaManager.getInstance().connectFederate();

        HlaManager.getInstance().addTickListener(SimController.getInstance());
        HlaManager.getInstance().addPhysicalEntityUpdatedListener(SimController.getInstance());

        SimController.getInstance().startSimEngine();
        SimController.getInstance().startSimGui();
        sleepSeconds(10);

        // TODO Population size as argument?
        Algorithm<FollowerEvaluationDataRow, NSGA2Chromosome> algorithm = new NSGA2<>(
                10,
                10,
                0.5,
                0.8,
                3,
                12
        );
        String[] exampleFileNames = new String[]{
                "experiment1/brooklyn-short.csv",
//                "experiment1/village.csv",
//                "experiment1/makland.csv"
        };
        Trainer trainer = new Trainer<>(
                FollowerUnit.class,
                FollowerEvaluationDataRow.class,
                new Experiment1FitnessEvaluator(2),
                algorithm,
                exampleFileNames
        );

        trainer.train(100000);
    }

    public static void main(String[] args) {
        Experiment1 experiment1 = new Experiment1();
        experiment1.run();
    }
}
