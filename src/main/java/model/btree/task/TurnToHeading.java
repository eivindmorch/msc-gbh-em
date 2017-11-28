package model.btree.task;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import model.btree.Blackboard;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.TurnToHeadingInteraction;
import unit.FollowerUnit;
import unit.Unit;
import util.Geometer;
import util.LlbmlUtil;
import util.exceptions.IllegalArgumentCombinationException;

public class TurnToHeading extends LeafTask<Blackboard> implements Named {

    private final String name = "Turn to heading";

    @Override
    public Status execute() {
        sendLLBMLTurnToHeadingTask(getObject().getFollowerUnit().getMarking());
        return Status.SUCCEEDED;
    }

    private void sendLLBMLTurnToHeadingTask(String entityMarkingString) {
        TurnToHeadingInteraction interaction = new TurnToHeadingInteraction();
        interaction.setTaskee(entityMarkingString);

        double deg;

        try {
            deg = calculateHeadingAngle();
        } catch (IllegalArgumentCombinationException e) {
            return;
        }

        deg = LlbmlUtil.normaliseDegForLlbml(deg);

        float rad = (float) Math.toRadians(deg);
        interaction.setHeading(rad);
        interaction.setTaskee(entityMarkingString);
        interaction.sendInteraction();
    }

    private double calculateHeadingAngle() throws IllegalArgumentCombinationException {
        FollowerUnit unit = getObject().getFollowerUnit();
        Unit otherUnit = getObject().getFollowerUnit().getTarget();

        return(Geometer.absoluteBearing(unit.getRawData().getLla(), otherUnit.getRawData().getLla()));
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
