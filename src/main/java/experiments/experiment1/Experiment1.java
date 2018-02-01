package experiments.experiment1;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import core.model.btree.BehaviorTreeUtil;
import core.model.btree.genops.Mutation;
import core.model.btree.genops.mutations.ReplaceWithSubtreeMutation;
import core.simulation.SimController;
import core.simulation.federate.Federate;
import core.training.Trainer;
import core.training.algorithms.Algorithm;
import core.training.algorithms.NSGA2.NSGA2;
import core.training.algorithms.NSGA2.NSGA2Chromosome;
import core.unit.UnitHandler;
import core.util.graphing.Grapher;
import experiments.experiment1.data.rows.FollowerEvaluationDataRow;
import experiments.experiment1.model.btree.task.unit.followerunit.MoveToTargetTask;
import experiments.experiment1.model.btree.task.unit.followerunit.TurnToTargetTask;
import experiments.experiment1.unit.Experiment1AddUnitMethod;
import experiments.experiment1.unit.Experiment1UnitInfo;
import experiments.experiment1.unit.FollowerUnit;


import java.lang.reflect.InvocationTargetException;

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
        SimController.getInstance().startSimGui();
        sleepSeconds(10);


        // TODO Population size as argument?
        Algorithm<FollowerEvaluationDataRow, NSGA2Chromosome> algorithm = new NSGA2<>(
                FollowerEvaluationDataRow.class,
                new Experiment1FitnessEvaluator(),
                20,
                0.5,
                0.8
        );

        String[] exampleFileNames = new String[]{
                "experiment1/brooklyn-simple-short.csv"
        };

        Trainer trainer = new Trainer<>(
                FollowerUnit.class,
                FollowerEvaluationDataRow.class,
                algorithm,
                exampleFileNames
        );


        trainer.train(10000);

//        sleepSeconds(20);
//        rti.destroy();
    }

}
