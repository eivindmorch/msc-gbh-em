package experiments.experiment1;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.BehaviorTreeUtil;
import core.training.Chromosome;
import core.simulation.Rti;
import core.simulation.SimController;
import core.simulation.federate.Federate;
import core.training.Population;
import core.training.Trainer;
import core.training.algorithms.Algorithm;
import core.training.algorithms.SimpleSingleObjectiveGA.SimpleSingleObjectiveGA;
import core.unit.UnitHandler;
import core.util.Grapher;
import core.util.exceptions.NoSuchTasksFoundException;
import experiments.experiment1.data.rows.FollowerEvaluationDataRow;
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

//        testCrossover();
//        testFlip();
//        testDelete();
//        testInsert();
//        testClean();
//        testMutate();
//        testGraphs();
//        testRandomiseTask();

        run();
    }

    private void run() {

        Rti.getInstance().start();

        sleepSeconds(5);
        Federate.getInstance().start();

        Federate.getInstance().addTickListener(SimController.getInstance());
        Federate.getInstance().addPhysicalEntityUpdatedListener(SimController.getInstance());

        SimController.getInstance().startSimEngine();
        SimController.getInstance().startSimGui();
        sleepSeconds(10);


        // TODO Population size as argument?
        Algorithm<FollowerEvaluationDataRow, Chromosome> algorithm = new SimpleSingleObjectiveGA<>(
                FollowerEvaluationDataRow.class,
                new Experiment1FitnessEvaluator()
        );

        String[] exampleFileNames = new String[]{
                "experiment1/brooklyn-without-R1.csv"
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

    private void testGraphs() {
        try {
            Task btree1 = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class);
            Grapher.graph(btree1);

            Task btree2 = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class);
            Grapher.graph(btree2);

            Task btree3 = BehaviorTreeUtil.crossover(btree1, btree2);
            Grapher.graph(btree3);

            sleepSeconds(7);
            Grapher.closeGraph(btree1);

            sleepSeconds(5);
            Population population = Population.generateRandomPopulation(10, FollowerUnit.class, Chromosome.class);
            Grapher.graph(population);

            sleepSeconds(5);
            Grapher.closeGraphs(population);

            sleepSeconds(5);
            Grapher.closeAllGraphs();

            sleepSeconds(5);
            Grapher.graph(population);

            run();

        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void testCrossover() {
        try {
            Task btree1 = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class);
            Grapher.graph(btree1);

            Task btree2 = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class);
            Grapher.graph(btree2);

            Task btree3 = BehaviorTreeUtil.crossover(btree1, btree2);
            Grapher.graph(btree3);

        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

//    private void testClean() {
//        try {
//            Task btree1 = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class);
//            Grapher grapher1 = new Grapher("P1 @" + Integer.toString(btree1.hashCode()));
//            grapher1.graph(btree1);
//
//            Grapher grapher3 = new Grapher("Child @" + Integer.toString(btree1.hashCode()));
//            grapher3.graph(BehaviorTreeUtil.removeEmptyAndSingleChildCompositeTasks(btree1));
//
//        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void testFlip() {
//        try {
//            Random random = new Random();
//
//            Task btree1 = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class);
//            Grapher grapher1 = new Grapher("P1 @" + Integer.toString(btree1.hashCode()));
//            grapher1.graph(btree1);
//
//            Task parent = BehaviorTreeUtil.getRandomCompositeTask(btree1, 2);
//
////            Task rnd1 = GenBehaviorTree.getRandomTask(btree1, true);
//            Task rnd1 = parent.getChild(random.nextInt(parent.getChildCount()));
//            Grapher grapher2 = new Grapher("R1 @" + Integer.toString(btree1.hashCode()));
//            grapher2.graph(rnd1);
//
////            Task rnd2 = GenBehaviorTree.getRandomTask(btree1, true);
//            Task rnd2 = parent.getChild(random.nextInt(parent.getChildCount()));
//            Grapher grapher3 = new Grapher("R2 @" + Integer.toString(btree1.hashCode()));
//            grapher3.graph(rnd2);
//
//            Task result = BehaviorTreeUtil.flipTwoTasks(btree1, rnd1, rnd2);
//            Grapher grapher4 = new Grapher("RESULT @" + Integer.toString(btree1.hashCode()));
//            grapher4.graph(result);
//
//        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void testDelete() {
//        try {
//            Task btree1 = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class);
//            Grapher grapher1 = new Grapher("P1 @" + Integer.toString(btree1.hashCode()));
//            grapher1.graph(btree1);
//
//            Task randTree = BehaviorTreeUtil.getRandomTask(btree1);
//            Grapher grapher3 = new Grapher("RANDOM TASK @" + Integer.toString(randTree.hashCode()));
//            grapher3.graph(randTree);
//
//            Task treeWithRemoved = BehaviorTreeUtil.removeTask(btree1, randTree);
//            Grapher grapher2 = new Grapher("RESULT @" + Integer.toString(treeWithRemoved.hashCode()));
//            grapher2.graph(treeWithRemoved);
//        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void testInsert() {
//        try {
//            Task btree1 = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class);
//            Grapher grapher1 = new Grapher("P1 @" + Integer.toString(btree1.hashCode()));
//            grapher1.graph(btree1);
//
//            Task randTree = BehaviorTreeUtil.getRandomTask(btree1);
//            Grapher grapher3 = new Grapher("RANDOM TASK @" + Integer.toString(randTree.hashCode()));
//            grapher3.graph(randTree);
//
//            Task parentTask = BehaviorTreeUtil.getRandomCompositeTask(btree1, 0);
//            Task treeWithInserted = BehaviorTreeUtil.insertTask(btree1, parentTask, randTree, parentTask.getChildCount());
//            Grapher grapher2 = new Grapher("RESULT @" + Integer.toString(treeWithInserted.hashCode()));
//            grapher2.graph(treeWithInserted);
//
//        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
//            e.printStackTrace();
//        }
//    }

    private void testRandomiseTask() {
        try {
            Class<FollowerUnit> unitClass = FollowerUnit.class;

            Task btree = BehaviorTreeUtil.generateRandomTree(unitClass);
            Grapher.graph(btree, "Original");

            Task randomTask = BehaviorTreeUtil.getRandomTask(btree, true);
            Grapher.graph(randomTask, "Random task");

            Task result = BehaviorTreeUtil.randomiseTask(btree, randomTask, unitClass);
            Grapher.graph(result, "Result");
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException | NoSuchTasksFoundException e) {
            e.printStackTrace();
        }

    }

    private void testMutate() {
        for (int i = 0; i < 1000000; i++) {
            try {
                Task btree = BehaviorTreeUtil.generateRandomTree(FollowerUnit.class);
//                Grapher grapher1 = new Grapher("P1 @" + Integer.toString(btree.hashCode()));
//                grapher1.graph(btree);

                Task mutatedTree = BehaviorTreeUtil.mutate(btree, FollowerUnit.class);
//                Grapher grapher2 = new Grapher("MUTATED @" + Integer.toString(mutatedTree.hashCode()));
//                grapher2.graph(mutatedTree);

            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }
}
