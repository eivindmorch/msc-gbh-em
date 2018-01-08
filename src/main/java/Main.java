import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import model.btree.Blackboard;
import model.btree.GenBehaviorTree;
import model.btree.task.unit.followerunit.IsApproaching;
import model.btree.task.unit.followerunit.IsCloseEnough;
import model.btree.task.unit.followerunit.Move;
import model.btree.task.unit.Wait;
import no.ffi.hlalib.datatypes.enumeratedData.CommandType;
import no.ffi.hlalib.datatypes.variantRecordData.CgfCommand;
import simulation.Rti;
import simulation.SimController;
import simulation.SimEngine;
import simulation.SimGui;
import simulation.federate.Federate;
import util.Grapher;

import static util.SystemUtil.sleepSeconds;

public class Main {

    public static void main(String[] args) {
        Main.run();
    }

    public static void run() {
        Rti rti = new Rti();
        rti.start();

        sleepSeconds(5);
        Federate federate = Federate.getInstance();
        federate.init();

        SimController simController = new SimController();

        federate.addTickListener(simController);
        federate.addPhysicalEntityUpdatedListener(simController);

        SimEngine simEngine = new SimEngine();
        simEngine.start();

        sleepSeconds(10);
        SimGui simGui = new SimGui();
        simGui.start();

        sleepSeconds(20);
        CgfCommand scenarioCommand = new CgfCommand();
        scenarioCommand.setCommand(CommandType.LoadScenario);
        scenarioCommand.getLoadScenario().setString("C:/MAK/vrforces4.5/userData/scenarios/it3903/follow_time-contrained-earth.scnx");
        Federate.getInstance().sendCgfControlInteraction(scenarioCommand);


        CgfCommand cmd = new CgfCommand();

        sleepSeconds(5);
        cmd.setCommand(CommandType.Play);
        Federate.getInstance().sendCgfControlInteraction(cmd);

        sleepSeconds(5);
        cmd.setCommand(CommandType.Pause);
        Federate.getInstance().sendCgfControlInteraction(cmd);

        sleepSeconds(5);
        cmd.setCommand(CommandType.Rewind);
        Federate.getInstance().sendCgfControlInteraction(cmd);

        sleepSeconds(5);
        cmd.setCommand(CommandType.Play);
        Federate.getInstance().sendCgfControlInteraction(cmd);

        sleepSeconds(5);
        cmd.setCommand(CommandType.Pause);
        Federate.getInstance().sendCgfControlInteraction(cmd);

        sleepSeconds(5);
        CgfCommand scenarioCommand2 = new CgfCommand();
        scenarioCommand2.setCommand(CommandType.LoadScenario);
        scenarioCommand2.getLoadScenario().setString("C:/MAK/vrforces4.5/userData/scenarios/it3903/follow_time-contrained-carrier.scnx");
        Federate.getInstance().sendCgfControlInteraction(scenarioCommand2);

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
}
