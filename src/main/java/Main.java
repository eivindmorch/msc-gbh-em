import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import model.btree.Blackboard;
import model.btree.GenBehaviorTree;
import model.btree.task.unit.followerunit.IsApproaching;
import model.btree.task.unit.followerunit.IsCloseEnough;
import model.btree.task.unit.followerunit.Move;
import model.btree.task.unit.Wait;
import model.btree.task.unit.followerunit.TurnToHeading;
import simulation.Rti;
import simulation.SimController;
import simulation.federate.Federate;
import unit.FollowerUnit;
import unit.Unit;
import util.Grapher;
import util.SystemStatus;

import java.util.HashMap;

import static util.SystemUtil.sleepSeconds;

public class Main {

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        run();
    }

    public void run() {
        // Setup
        Rti.getInstance().start();

        sleepSeconds(5);
        Federate.getInstance().start();

        setControlledUnitBtreeMap(testBtree());

        Federate.getInstance().addTickListener(SimController.getInstance());
        Federate.getInstance().addPhysicalEntityUpdatedListener(SimController.getInstance());

        sleepSeconds(5);
        SimController.getInstance().startSimEngine();

        sleepSeconds(10);
        SimController.getInstance().startSimGui();


        // Tests
        sleepSeconds(10);
        SimController.getInstance().loadScenario(
                "C:/MAK/vrforces4.5/userData/scenarios/it3903/follow_time-contrained-makland.scnx"
        );

        sleepSeconds(10);
        SimController.getInstance().play();

        sleepSeconds(10);
        SimController.getInstance().pause();

        sleepSeconds(10);
        SimController.getInstance().rewind();

        sleepSeconds(10);
        SimController.getInstance().play();

        sleepSeconds(10);
        SimController.getInstance().pause();

        sleepSeconds(10);
        SimController.getInstance().loadScenario(
                "C:/MAK/vrforces4.5/userData/scenarios/it3903/follow_time-contrained-earth.scnx"
        );
        sleepSeconds(10);
        SimController.getInstance().play();

//        sleepSeconds(20);
//        rti.destroy();
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

    public GenBehaviorTree testBtree() {
        Sequence waitAndTurnToSequence = new Sequence(new Wait(), new TurnToHeading());
        Selector shouldMoveSelector = new Selector(new IsApproaching(15), new IsCloseEnough(5));
        Sequence shouldNotMoveSequence = new Sequence(shouldMoveSelector, waitAndTurnToSequence);
        Selector waitOrMoveSelector = new Selector(shouldNotMoveSequence, new Move());
        return new GenBehaviorTree(waitOrMoveSelector);
    }

    private void setControlledUnitBtreeMap(GenBehaviorTree btree) {
        HashMap<Class<? extends Unit>, GenBehaviorTree> controlledUnitBtreeMap = new HashMap<>();
        controlledUnitBtreeMap.put(FollowerUnit.class, btree);
        SystemStatus.controlledUnitBtreeMap = controlledUnitBtreeMap;
    }
}
