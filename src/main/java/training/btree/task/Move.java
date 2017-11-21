package training.btree.task;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.MoveInteraction;
import training.btree.Blackboard;


public class Move extends LeafTask<Blackboard> implements Named {

    private final String name = "Move";

    @Override
    public Status execute() {
        sendLLBMLMoveTask(getObject().unit.role.name());
        return Status.SUCCEEDED;
    }

    private void sendLLBMLMoveTask(String entityMarkingString){
        MoveInteraction interaction = new MoveInteraction();
        // TODO Use blackboard data to calc angle
        double deg = 150;
        float rad = (float) Math.toRadians(deg + 90); // TODO Change if base method is changed to use azimuth
        interaction.setDirection(rad);
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
