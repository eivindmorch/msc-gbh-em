package training.btree.task;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.WaitInteraction;
import training.btree.Blackboard;

public class Wait extends LeafTask<Blackboard> implements Named {

    private final String name = "Wait";

    @Override
    public Status execute() {
        sendLLBMLWaitTask(getObject().unit.role.name());
        return Status.SUCCEEDED;
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
