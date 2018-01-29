package experiments.experiment1.model.btree.task.unit.followerunit;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import core.model.btree.task.NamedTask;
import core.model.btree.task.TaskTickTracker;
import core.model.btree.task.VariableTask;
import experiments.experiment1.unit.FollowerUnit;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.FollowUnitInteraction;

import static core.util.SystemUtil.random;

public class FollowTarget extends LeafTask<Blackboard<FollowerUnit>> implements NamedTask, VariableTask {

    private int ticksToRun;
    private TaskTickTracker tickTracker;
    private String name;

    public FollowTarget() {
        randomiseTicksToRun();
    }

    public FollowTarget(int ticksToRun) {
        setTicksToRun(ticksToRun);
    }

    public FollowTarget(FollowTarget followUnit) {
        this(followUnit.ticksToRun);
    }

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
        return new FollowTarget();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void randomiseVariables() {
        randomiseTicksToRun();
    }

    private void randomiseTicksToRun() {
        setTicksToRun(1 + random.nextInt(9));
    }

    private void setTicksToRun(int ticksToRun) {
        this.ticksToRun = ticksToRun;
        this.tickTracker = new TaskTickTracker(ticksToRun);
        this.name = "Follow target (" + ticksToRun + ")";
    }
}
