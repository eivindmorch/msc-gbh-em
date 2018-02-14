package experiments.experiment1.model.btree.task.unit.followerunit;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import core.model.btree.task.AlwaysSuccessfulTask;
import core.model.btree.task.NamedTask;
import core.model.btree.task.TaskTickTracker;
import experiments.experiment1.unit.FollowerUnit;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.FollowUnitInteraction;


public class FollowTargetTask extends LeafTask<Blackboard<FollowerUnit>> implements NamedTask, AlwaysSuccessfulTask {

    private String name = "Follow target";
    private final TaskTickTracker taskTickTracker = new TaskTickTracker(1);

    @Override
    public Task.Status execute() {
        if (taskTickTracker.getCurrentStatus() == TaskTickTracker.Status.FIRST) {
            sendLLBMLFollowUnitTask();
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

    private void sendLLBMLFollowUnitTask(){
        FollowUnitInteraction interaction = new FollowUnitInteraction();

        interaction.setUnit(getObject().getUnit().getTarget().getMarking());
        interaction.setTaskee(getObject().getUnit().getMarking());
        interaction.sendInteraction();
    }

    @Override
    protected Task<Blackboard<FollowerUnit>> copyTo(Task<Blackboard<FollowerUnit>> task) {
        return task;
    }

    @Override
    public String getName() {
        return name;
    }

}
