package model.btree.task.unit.followerunit;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import model.btree.Blackboard;
import model.btree.task.Named;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.TurnToHeadingInteraction;
import unit.FollowerUnit;
import unit.Unit;
import util.Geometer;
import util.LlbmlUtil;
import util.exceptions.IllegalArgumentCombinationException;

public class TurnToHeading extends LeafTask<Blackboard<FollowerUnit>> implements Named {

    private final String name = "Turn to heading";

    @Override
    public Status execute() {
        sendLLBMLTurnToHeadingTask(getObject().getUnit().getMarking());
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
        FollowerUnit unit = getObject().getUnit();
        Unit otherUnit = getObject().getUnit().getTarget();

        return(Geometer.absoluteBearing(unit.getRawDataRow().getLla(), otherUnit.getRawDataRow().getLla()));
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
