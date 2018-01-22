package experiments.experiment1;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.GenBehaviorTree;
import core.simulation.Rti;
import core.simulation.SimController;
import core.simulation.federate.Federate;
import core.training.Trainer;
import core.training.algorithms.Algorithm;
import core.training.algorithms.SimpleSingleObjectiveGA;
import core.unit.UnitHandler;
import core.util.Grapher;
import experiments.experiment1.data.rows.FollowerEvaluationDataRow;
import experiments.experiment1.model.btree.task.unit.followerunit.Move;
import experiments.experiment1.unit.Experiment1AddUnitMethod;
import experiments.experiment1.unit.Experiment1UnitInfo;
import experiments.experiment1.unit.FollowerUnit;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import static core.util.SystemUtil.sleepSeconds;

public class Experiment1 {

    public static void main(String[] args) {
        new Experiment1();
    }

    private Experiment1() {
        Experiment1UnitInfo.init();
        UnitHandler.setAddUnitMethod(new Experiment1AddUnitMethod());
//        testCrossover();
//        run();
//        testFlip();
        testClean();
    }


    private void run() {

        Rti.getInstance().start();

        sleepSeconds(5);
        SimController.getInstance().startSimEngine();
        SimController.getInstance().startSimGui();

        sleepSeconds(5);
        Federate.getInstance().start();

        Federate.getInstance().addTickListener(SimController.getInstance());
        Federate.getInstance().addPhysicalEntityUpdatedListener(SimController.getInstance());

        sleepSeconds(10);


        // TODO Population size as argument?
        Algorithm<FollowerEvaluationDataRow> algorithm = new SimpleSingleObjectiveGA<>(
                FollowerEvaluationDataRow.class,
                new Experiment1FitnessEvaluator()
        );

        String[] exampleFileNames = new String[]{
                "experiment1/0.csv"
        };

        Trainer trainer = new Trainer<>(
                FollowerUnit.class,
                FollowerEvaluationDataRow.class,
                algorithm,
                exampleFileNames
        );

        trainer.train(3);
        Grapher.graphPopulation(trainer.getPopulation());

        LeafTask move = new Move();

//        sleepSeconds(20);
//        rti.destroy();
    }

    private void testCrossover() {
        try {
            GenBehaviorTree btree1 = GenBehaviorTree.generateRandomTree(FollowerUnit.class);
            Grapher grapher1 = new Grapher("P1 @" + Integer.toString(btree1.hashCode()));
            grapher1.graph(btree1);

            GenBehaviorTree btree2 = GenBehaviorTree.generateRandomTree(FollowerUnit.class);
            Grapher grapher2 = new Grapher("P2 @" + Integer.toString(btree2.hashCode()));
            grapher2.graph(btree2);

            GenBehaviorTree btree3 = GenBehaviorTree.crossover(btree1, btree2);
            Grapher grapher3 = new Grapher("Child @" + Integer.toString(btree2.hashCode()));
            grapher3.graph(btree3);

        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void testClean() {
        try {
            GenBehaviorTree btree1 = GenBehaviorTree.generateRandomTree(FollowerUnit.class);
            Grapher grapher1 = new Grapher("P1 @" + Integer.toString(btree1.hashCode()));
            grapher1.graph(btree1);

            Grapher grapher3 = new Grapher("Child @" + Integer.toString(btree1.hashCode()));
            grapher3.graph(GenBehaviorTree.cloneAndRemoveEmptyAndSingleChildBranchTasks(btree1.getRoot()));

        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void testFlip() {
        try {
            Random random = new Random();

            GenBehaviorTree btree1 = GenBehaviorTree.generateRandomTree(FollowerUnit.class);
            Grapher grapher1 = new Grapher("P1 @" + Integer.toString(btree1.hashCode()));
            grapher1.graph(btree1);

            Task parent = GenBehaviorTree.getRandomTask(btree1.getRoot(), true);

//            Task rnd1 = GenBehaviorTree.getRandomTask(btree1, true);
            Task rnd1 = parent.getChild(random.nextInt(parent.getChildCount()));
            Grapher grapher2 = new Grapher("R1 @" + Integer.toString(btree1.hashCode()));
            System.out.println(rnd1);
            grapher2.graph(rnd1);

//            Task rnd2 = GenBehaviorTree.getRandomTask(btree1, true);
            Task rnd2 = parent.getChild(random.nextInt(parent.getChildCount()));
            System.out.println(rnd2);
            Grapher grapher3 = new Grapher("R2 @" + Integer.toString(btree1.hashCode()));
            grapher3.graph(rnd2);

            Task result = GenBehaviorTree.cloneBehaviorTreeAndFlipTwoSubtrees(btree1.getRoot(), rnd1, rnd2);
            Grapher grapher4 = new Grapher("RESULT @" + Integer.toString(btree1.hashCode()));
            grapher4.graph(result);


        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
