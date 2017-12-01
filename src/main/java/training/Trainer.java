package training;

import com.badlogic.gdx.ai.btree.branch.Sequence;
import simulation.federate.Federate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import model.btree.GenBehaviorTree;
import model.btree.Blackboard;
import model.btree.task.follower.IsCloseEnough;
import model.btree.task.follower.IsApproaching;
import model.btree.task.follower.Move;
import com.badlogic.gdx.ai.btree.branch.Selector;
import data.FollowerProcessedDataRow;
import model.btree.task.general.Wait;
import util.Grapher;
import util.Reader;
import simulation.SimEngine;
import util.SystemStatus;

import java.util.List;

import static util.Settings.*;

public class Trainer {

    private FollowerProcessedDataRow exampleData, iterationData;
    private double fitness;
    private final Logger logger = LoggerFactory.getLogger(Trainer.class);

    public void init() {
    }

    private void runTrainingRound(Population population, Example example) {
        SystemStatus.currentScenario = example.getScenario();
        // TODO run simulation
        population.evaluatePopulation(example);
        population.selectNextPopulation();
    }

    private void evaluate(Example example) {
        Reader exampleDataReader = new Reader(exampleDataFilePath);
        Reader iterationDataReader = new Reader(iterationDataFilePath);

        List<String> exampleDataLine = exampleDataReader.readLine();
        List<String> iterationDataLine = iterationDataReader.readLine();

        while (exampleDataLine != null && iterationDataLine != null) {
            FollowerProcessedDataRow exampleData = new FollowerProcessedDataRow(exampleDataLine);
            FollowerProcessedDataRow iterationData = new FollowerProcessedDataRow(iterationDataLine);

            fitness += distanceFitness(exampleData.getDistanceToTarget(), iterationData.getDistanceToTarget());

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
        federate.init();

        SimEngine simEngine = new SimEngine();
        simEngine.init();
    }
}
