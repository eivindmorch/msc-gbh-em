package training;

import com.badlogic.gdx.ai.btree.branch.Sequence;
import federate.Federate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import model.btree.GenBehaviorTree;
import model.btree.Blackboard;
import model.btree.task.IsCloseEnough;
import model.btree.task.IsApproaching;
import model.btree.task.Move;
import com.badlogic.gdx.ai.btree.branch.Selector;
import data.ProcessedData;
import model.btree.task.Wait;
import util.Grapher;
import util.Reader;
import util.SimEngine;

import java.util.List;

import static util.Values.*;

public class Trainer {

    private ProcessedData exampleData, iterationData;
    private double fitness;
    private final Logger logger = LoggerFactory.getLogger(Trainer.class);

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

    public static void main(String[] args) {
        Trainer trainer = new Trainer();
        trainer.behaviorTreeTest();
//        trainer.simEngineTest();
    }

    private void behaviorTreeTest() {
        Selector<Blackboard> selector1 = new Selector(new IsApproaching(), new IsCloseEnough());
        Sequence<Blackboard> sequence1 = new Sequence(selector1, new Wait());
        Sequence<Blackboard> sequence2 = new Sequence(sequence1, new Move());
        GenBehaviorTree btree = new GenBehaviorTree(sequence2, new Blackboard(null));
        Grapher grapher = new Grapher("Original");
        grapher.graph(btree);

        Grapher grapher1 = new Grapher("Clone");
        grapher1.graph(btree.clone());

        Grapher grapher2 = new Grapher("Clone with insertion");
        grapher2.graph(btree.cloneAndInsertChild(sequence1, new Move(), 2));
    }

    private void simEngineTest() {
        Federate federate = new Federate();
        federate.initiate();

        SimEngine simEngine = new SimEngine();
        simEngine.initiate();
    }
}
