package core.model.btree.task.unit;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import core.model.btree.task.NamedTask;
import core.model.btree.task.TaskTickTracker;
import core.model.btree.task.VariableLeafTask;
import core.unit.Unit;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.WaitInteraction;

import static core.util.SystemUtil.random;


public class WaitTask extends VariableLeafTask<Blackboard<Unit>> implements NamedTask {

    private int ticksToRun;
    private TaskTickTracker tickTracker;
    private String name;

    public WaitTask(){
        randomiseTicksToRun();
    }

    public WaitTask(int ticksToRun) {
        setTicksToRun(ticksToRun);
    }

    public WaitTask(WaitTask waitTask) {
        this(waitTask.ticksToRun);
    }

    @Override
    public Status execute() {
        if (tickTracker.getCurrentTick() == 0) {
            sendLLBMLWaitTask(getObject().getUnit().getMarking());
        }
        return tickTracker.tick();
    }

    @Override
    public void start() {
        super.start();
        this.tickTracker = new TaskTickTracker(ticksToRun);
    }

    private void sendLLBMLWaitTask(String entityMarkingString){
        WaitInteraction interaction = new WaitInteraction();
        interaction.setTaskee(entityMarkingString);
        interaction.sendInteraction();
    }

    @Override
    protected Task<Blackboard<Unit>> copyTo(Task<Blackboard<Unit>> task) {
        return new WaitTask(this);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void randomiseVariables() {
        randomiseTicksToRun();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WaitTask && this.ticksToRun == ((WaitTask) obj).ticksToRun;
    }

    private void randomiseTicksToRun() {
        setTicksToRun(1 + random.nextInt(9));
    }

    private void setTicksToRun(int ticksToRun) {
        this.ticksToRun = ticksToRun;
        this.name = "Wait (" + ticksToRun + ")";
    }
}
