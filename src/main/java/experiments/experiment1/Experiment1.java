package experiments.experiment1;

import core.btree.BehaviorTreeUtil;
import core.simulation.SimController;
import core.training.Trainer;
import core.training.algorithms.Algorithm;
import core.training.algorithms.NSGA2.NSGA2;
import core.training.algorithms.NSGA2.NSGA2Chromosome;
import core.unit.ControlledUnit;
import core.unit.UnitLogger;
import core.util.SystemStatus;
import experiments.ExperimentInitialiser;
import experiments.experiment1.datarows.FollowerEvaluationDataRow;
import experiments.experiment1.units.FollowerUnit;

public abstract class Experiment1 {


    private static void record() {
        ExperimentInitialiser.setup(
                new Experiment1UnitTypeInfoInitialiser(),
                new Experiment1AddUnitMethod(),
                false,
                false,
                false
        );

        UnitLogger.setIntraResourcesWritingDirectory("data/example_logging/" + SystemStatus.START_TIME_STRING + "/");
        ControlledUnit.setControlledUnitBtreeMap(FollowerUnit.class, BehaviorTreeUtil.generateTestTree());
        SimController.getInstance().play();
    }

    private static void train() {
        ExperimentInitialiser.setup(
                new Experiment1UnitTypeInfoInitialiser(),
                new Experiment1AddUnitMethod(),
                false,
                true,
                false
        );

        Algorithm<FollowerEvaluationDataRow, NSGA2Chromosome> algorithm = new NSGA2<>(
                10,
                10,
                0.5,
                0.8,
                3,
                12
        );
        String[] exampleFileNames = new String[]{
                "experiment1/brooklyn-display.csv",
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
        Experiment1.train();
    }
}
