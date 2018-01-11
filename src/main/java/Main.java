import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import model.btree.Blackboard;
import model.btree.GenBehaviorTree;
import model.btree.task.unit.followerunit.IsApproaching;
import model.btree.task.unit.followerunit.IsCloseEnough;
import model.btree.task.unit.followerunit.Move;
import model.btree.task.unit.Wait;
import simulation.Rti;
import simulation.SimController;
import simulation.federate.Federate;
import unit.ControlledUnit;
import unit.FollowerUnit;
import unit.Unit;
import util.Grapher;

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

        ControlledUnit.setControlledUnitBtreeMap(FollowerUnit.class, GenBehaviorTree.generateTestTree());

        Federate.getInstance().addTickListener(SimController.getInstance());
        Federate.getInstance().addPhysicalEntityUpdatedListener(SimController.getInstance());

        sleepSeconds(5);
        SimController.getInstance().startSimEngine();

        sleepSeconds(10);
        SimController.getInstance().startSimGui();


        // Tests
        sleepSeconds(10);
        SimController.getInstance().loadScenario(
                "C:/MAK/vrforces4.5/userData/scenarios/it3903/follow_time-contrained-earth.scnx"
        );

        sleepSeconds(10);
        SimController.getInstance().play();
//        while (true) {
//            sleepSeconds(10);
//            SimController.getInstance().rewind();
//            sleepSeconds(2);
//            SimController.getInstance().play();
//        }


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
        Selector<Blackboard<FollowerUnit>> selector1 = new Selector<>(new IsApproaching(), new IsCloseEnough());
        Sequence<Blackboard<? extends Unit>> sequence1 = new Sequence(selector1, new Wait());
        Sequence<Blackboard<? extends Unit>> sequence2 = new Sequence(sequence1, new Move());
        GenBehaviorTree btree = new GenBehaviorTree(sequence2, new Blackboard<FollowerUnit>(null));
        Grapher grapher = new Grapher("Original");
        grapher.graph(btree);

        Grapher grapher1 = new Grapher("Clone");
        grapher1.graph(btree.clone());

        Grapher grapher2 = new Grapher("Clone with insertion");
        grapher2.graph(btree.cloneAndInsertChild(sequence1, new Move(), 2));
    }

}
