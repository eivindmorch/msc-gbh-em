package training.btree.task;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import data.LlaData;
import datalogging.Unit;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.TurnToHeadingInteraction;
import training.btree.Blackboard;
import util.Calculations;


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
        double deg = calculateMovementAngle();
        deg -= 90;
        deg = 360 - deg;

        while (deg >= 360) {
            deg -= 360;
        }

        float rad = (float) Math.toRadians(deg);
        interaction.setHeading(rad);
        interaction.setTaskee(getObject().unit.role.name());
        interaction.sendInteraction();
    }

    private double calculateMovementAngle() {
        Unit unit = getObject().unit;
        Unit otherUnit = getObject().otherUnit;

        LlaData lla1 = Calculations.ecefToLla(unit.rawData.posVector);
        LlaData lla2 = Calculations.ecefToLla(otherUnit.rawData.posVector);
        double angle = Calculations.geodlib(lla1, lla2);

        return(angle);
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
