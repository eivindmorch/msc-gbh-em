package experiments.experiment1.model.btree.task.unit.followerunit;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import core.model.btree.task.NamedTask;
import core.model.btree.task.TaskTickTracker;
import core.model.btree.task.unit.VariableTask;
import experiments.experiment1.unit.FollowerUnit;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.FollowUnitInteraction;

public class FollowUnit extends LeafTask<Blackboard<FollowerUnit>> implements NamedTask, VariableTask {

    private TaskTickTracker tickTracker = new TaskTickTracker(5);

    @Override
    public Status execute() {
        if (tickTracker.getCurrentTick() == 0) {
            sendLLBMLFollowUnitTask();
        }
        return tickTracker.tick();
    }

    private void sendLLBMLFollowUnitTask(){
        FollowUnitInteraction interaction = new FollowUnitInteraction();

        interaction.setUnit(getObject().getUnit().getTarget().getMarking());
        interaction.setTaskee(getObject().getUnit().getMarking());
        interaction.sendInteraction();
    }

    @Override
    protected Task<Blackboard<FollowerUnit>> copyTo(Task<Blackboard<FollowerUnit>> task) {
        return null;
    }

    @Override
    public Task<Blackboard<FollowerUnit>> cloneTask() {
        return new FollowUnit();
    }

    @Override
    public String getName() {
        return "FollowUnit";
    }
}
