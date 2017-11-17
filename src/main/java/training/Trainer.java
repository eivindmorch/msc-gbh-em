package training;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import training.btree.task.IsCloseEnough;
import training.btree.task.IsApproaching;
import training.btree.task.Move;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.branch.Selector;
import data.ProcessedData;
import training.btree.task.Wait;
import util.Grapher;
import util.Reader;

import java.util.List;

import static util.Values.*;

public class Trainer {

    private ProcessedData exampleData, iterationData;
    private double fitness;

    private void run() {
        Reader exampleDataReader = new Reader(exampleDataFilePath);
        Reader iterationDataReader = new Reader(iterationDataFilePath);

        List<String> exampleDataLine = exampleDataReader.readLine();
        List<String> iterationDataLine = iterationDataReader.readLine();

        while (exampleDataLine != null && iterationDataLine != null) {
            ProcessedData exampleData = new ProcessedData(exampleDataLine);
            ProcessedData iterationData = new ProcessedData(iterationDataLine);

            fitness += distanceFitness(exampleData.getDistance(), iterationData.getDistance());

            exampleDataLine = exampleDataReader.readLine();
            iterationDataLine = iterationDataReader.readLine();
            System.out.println(fitness);
        }
    }

    private double distanceFitness(double exampleDistance, double iterationDistance) {
        return Math.pow(Math.abs(exampleDistance - iterationDistance), 2);
    }

    private void behaviorTreeTest() {
        Selector selector1 = new Selector(new IsApproaching(), new IsCloseEnough());
        Sequence sequence1 = new Sequence(selector1, new Wait());
        Sequence sequence2 = new Sequence(sequence1, new Move());
        BehaviorTree btree = new BehaviorTree(sequence2);
        Grapher grapher = new Grapher();
        grapher.graph(btree);
    }

    private Task cloneBehaviorTree(Task btree) {
        return cloneBehaviorTreeAndInsertChild(btree, null, null, -1);
    }

    private Task cloneBehaviorTreeAndInsertChild(Task btree, Task insertParent, Task insertChild, int insertIndex) {
        Task newBtree = instantiateTaskObject(btree);

        for (int i = 0; i < btree.getChildCount(); i++) {
            if (btree == insertParent && i == insertIndex) {
                newBtree.addChild(insertChild);
            }
            Task child = cloneBehaviorTreeAndInsertChild(btree.getChild(i), insertParent, insertChild, insertIndex);
            newBtree.addChild(child);
        }
        return newBtree;
    }

    private Task instantiateTaskObject(Task task) {
        Task newTask = null;
        try {
            newTask = task.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return newTask;
    }

    public static void main(String[] args) {
        Trainer trainer = new Trainer();
//        trainer.run();
        trainer.behaviorTreeTest();
    }


}
