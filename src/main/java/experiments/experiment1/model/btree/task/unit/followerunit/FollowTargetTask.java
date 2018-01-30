package experiments.experiment1.model.btree.task.unit.followerunit;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import core.model.btree.task.NamedTask;
import core.model.btree.task.TaskTickTracker;
import core.model.btree.task.VariableLeafTask;
import experiments.experiment1.unit.FollowerUnit;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.FollowUnitInteraction;

import static core.util.SystemUtil.random;

public class FollowTargetTask extends VariableLeafTask<Blackboard<FollowerUnit>> implements NamedTask {

    private int ticksToRun;
    private TaskTickTracker tickTracker;
    private String name;

    public FollowTargetTask() {
        randomiseTicksToRun();
    }

    public FollowTargetTask(int ticksToRun) {
        setTicksToRun(ticksToRun);
    }

    public FollowTargetTask(FollowTargetTask followUnit) {
        this(followUnit.ticksToRun);
    }

    @Override
    public Status execute() {
        if (tickTracker.getCurrentTick() == 0) {
            sendLLBMLFollowUnitTask();
        }
        return tickTracker.tick();
    }

    @Override
    public void start() {
        super.start();
        this.tickTracker = new TaskTickTracker(ticksToRun);
    }

    private void sendLLBMLFollowUnitTask(){
        FollowUnitInteraction interaction = new FollowUnitInteraction();

        interaction.setUnit(getObject().getUnit().getTarget().getMarking());
        interaction.setTaskee(getObject().getUnit().getMarking());
        interaction.sendInteraction();
    }

    @Override
    protected Task<Blackboard<FollowerUnit>> copyTo(Task<Blackboard<FollowerUnit>> task) {
        return new FollowTargetTask(this);
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
        this.name = "Follow target (" + ticksToRun + ")";
    }
}
