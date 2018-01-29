package core.model.btree.task.unit;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import core.model.btree.task.NamedTask;
import core.model.btree.task.TaskTickTracker;
import core.model.btree.task.VariableTask;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.WaitInteraction;

import static core.util.SystemUtil.random;


public class Wait extends LeafTask<Blackboard> implements NamedTask, VariableTask {

    private int ticksToRun;
    private TaskTickTracker tickTracker;
    private String name;

    public Wait(){
        randomiseTicksToRun();
    }

    public Wait(int ticksToRun) {
        setTicksToRun(ticksToRun);
    }

    public Wait(Wait wait) {
        this(wait.ticksToRun);
    }

    @Override
    public Status execute() {
        if (tickTracker.getCurrentTick() == 0) {
            sendLLBMLWaitTask(getObject().getUnit().getMarking());
        }
        return tickTracker.tick();
    }

    private void sendLLBMLWaitTask(String entityMarkingString){
        WaitInteraction interaction = new WaitInteraction();
        interaction.setTaskee(entityMarkingString);
        interaction.sendInteraction();
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        return task;
    }

    @Override
    public String getName() {
        return this.name;
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
        this.name = "Wait (" + ticksToRun + ")";
    }
}
