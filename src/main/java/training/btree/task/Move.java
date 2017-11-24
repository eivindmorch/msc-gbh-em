package training.btree.task;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import datalogging.Unit;
import util.exceptions.IllegalArgumentCombinationException;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.TurnToHeadingInteraction;
import training.btree.Blackboard;
import util.Geometer;


public class Move extends LeafTask<Blackboard> implements Named {

    private final String name = "Move";

    @Override
    public Status execute() {
        sendLLBMLTurnToHeadingTask();
        return Status.SUCCEEDED;
    }

    // TODO Investigate issues with MoveTask in HlaLib
    private void sendLLBMLTurnToHeadingTask(){
        TurnToHeadingInteraction interaction = new TurnToHeadingInteraction();
        double deg;

        try {
            deg = calculateMovementAngle();
        } catch (IllegalArgumentCombinationException e) {
            return;
        }

        deg = normaliseDegForLlbml(deg);

        float rad = (float) Math.toRadians(deg);
        interaction.setHeading(rad);
        interaction.setTaskee(getObject().unit.getRole().name());
        interaction.sendInteraction();
    }

    private double calculateMovementAngle() throws IllegalArgumentCombinationException {
        Unit unit = getObject().unit;
        Unit otherUnit = getObject().otherUnit;

        return(Geometer.absoluteBearing(unit.getRawData().getLla(), otherUnit.getRawData().getLla()));
    }

    private double normaliseDegForLlbml(double deg) {
        deg = 360 - deg; // Convert from counter-clockwise to clockwise
        deg += 90;       // Make north 0 degrees instead of east
        return deg;
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
