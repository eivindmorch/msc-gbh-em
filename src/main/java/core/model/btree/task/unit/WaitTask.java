package core.model.btree.task.unit;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import core.model.btree.task.AlwaysSuccessfulTask;
import core.model.btree.task.NamedTask;
import core.model.btree.task.TaskTickTracker;
import core.model.btree.task.VariableLeafTask;
import core.unit.Unit;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.WaitInteraction;

import static core.util.SystemUtil.random;


public class WaitTask extends LeafTask<Blackboard<Unit>> implements NamedTask, AlwaysSuccessfulTask {

    private String name = "Wait";
    private final TaskTickTracker taskTickTracker = new TaskTickTracker(1);

    @Override
    public Task.Status execute() {
        if (taskTickTracker.getCurrentStatus() == TaskTickTracker.Status.FIRST) {
            sendLLBMLWaitTask();
        }
        taskTickTracker.tick();
        if (taskTickTracker.getCurrentStatus() == TaskTickTracker.Status.DONE) {
            return Status.SUCCEEDED;
        }
        return Task.Status.RUNNING;
    }

    @Override
    public void start() {
        super.start();
        taskTickTracker.reset();
    }

    private void sendLLBMLWaitTask(){
        WaitInteraction interaction = new WaitInteraction();
        interaction.setTaskee(getObject().getUnit().getMarking());
        interaction.sendInteraction();
    }

    @Override
    protected Task<Blackboard<Unit>> copyTo(Task<Blackboard<Unit>> task) {
        return task;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
