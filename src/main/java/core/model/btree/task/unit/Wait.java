package core.model.btree.task.unit;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import core.model.btree.task.NamedTask;
import core.model.btree.task.TaskTickTracker;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.WaitInteraction;

public class Wait extends LeafTask<Blackboard> implements NamedTask {

    private final String name = "Wait";

    private TaskTickTracker tickTracker = new TaskTickTracker(1);


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

}
