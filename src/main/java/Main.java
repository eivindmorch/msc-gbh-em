import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import model.btree.Blackboard;
import model.btree.GenBehaviorTree;
import model.btree.task.follower.IsApproaching;
import model.btree.task.follower.IsCloseEnough;
import model.btree.task.follower.Move;
import model.btree.task.general.Wait;
import simulation.SimController;
import simulation.federate.Federate;
import util.Grapher;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Main.run();
    }

    public static void run() {
        Federate federate = Federate.getInstance();
        federate.init();

        SimController simController = new SimController();

        federate.addTickListener(simController);
        federate.addPhysicalEntityUpdatedListener(simController);

//        simController.startSimEngine();

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        simController.resume();

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        simController.freeze();

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        simController.freeze();

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        simController.resume();

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
}
