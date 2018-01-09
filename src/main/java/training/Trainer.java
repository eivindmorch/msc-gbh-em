package training;

import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import model.btree.GenBehaviorTree;
import model.btree.task.unit.followerunit.IsApproaching;
import model.btree.task.unit.followerunit.IsCloseEnough;
import model.btree.task.unit.followerunit.Move;
import model.btree.task.unit.followerunit.TurnToHeading;
import model.btree.task.unit.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.TrainingSettings;
import simulation.Rti;
import simulation.SimController;
import simulation.federate.Federate;
import training.algorithms.Algorithm;
import unit.FollowerUnit;
import unit.Unit;
import util.SystemMode;
import util.SystemStatus;

import java.util.HashMap;

import static util.SystemUtil.sleepSeconds;


public class Trainer {

    private final Logger logger = LoggerFactory.getLogger(Trainer.class);

    private Algorithm algorithm;
    private boolean running;

    public Trainer() {
        SystemStatus.systemMode = SystemMode.TRAINING;
        try {
            algorithm = TrainingSettings.algorithm.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        Rti.getInstance().start();

        sleepSeconds(5);
        Federate.getInstance().start();

        setControlledUnitBtreeMap(testBtree());

        Federate.getInstance().addTickListener(SimController.getInstance());
        Federate.getInstance().addPhysicalEntityUpdatedListener(SimController.getInstance());

//        simController.startSimEngine();
    }

    public void run() {
        SimController.getInstance().rewind();
        algorithm.setup();
        while (running) {
            for (int i = 0; i < TrainingSettings.epochs; i++) {
                for (String example : TrainingSettings.examples) {
                    simulatePopulation();
                    algorithm.epoch();
                }
            }
        }
        // ParetoPlotter.plot();
    }

    private void simulatePopulation() {
        for (int i = 0; i < TrainingSettings.populationSize; i++) {
            // TODO
//            setControlledUnitBtreeMap(population.get(i));
            setControlledUnitBtreeMap(testBtree());
            SimController.getInstance().play();
            SimController.getInstance().rewind();
        }
    }

    public GenBehaviorTree testBtree() {
        Sequence waitAndTurnToSequence = new Sequence(new Wait(), new TurnToHeading());
        Selector shouldMoveSelector = new Selector(new IsApproaching(15), new IsCloseEnough(5));
        Sequence shouldNotMoveSequence = new Sequence(shouldMoveSelector, waitAndTurnToSequence);
        Selector waitOrMoveSelector = new Selector(shouldNotMoveSequence, new Move());
        return new GenBehaviorTree(waitOrMoveSelector);
    }

    // TODO Extend to handle multiple unit classes
    private void setControlledUnitBtreeMap(GenBehaviorTree btree) {
        HashMap<Class<? extends Unit>, GenBehaviorTree> controlledUnitBtreeMap = new HashMap<>();
        controlledUnitBtreeMap.put(FollowerUnit.class, btree);
        SystemStatus.controlledUnitBtreeMap = controlledUnitBtreeMap;
    }

    public static void main(String[] args) {
        Trainer trainer = new Trainer();
    }




}
