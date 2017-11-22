package training.btree.task;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import datalogging.Unit;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.MoveInteraction;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.MoveToUnitInteraction;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import training.btree.Blackboard;
import util.Calculations;


public class Move extends LeafTask<Blackboard> implements Named {

    private final String name = "Move";

    @Override
    public Status execute() {
        sendLLBMLMoveTask();
//        sendLLBMLMoveToUnitTask();
        return Status.SUCCEEDED;
    }

    private void sendLLBMLMoveToUnitTask(){
        MoveToUnitInteraction interaction = new MoveToUnitInteraction();
        interaction.setUnit(getObject().otherUnit.role.name());
        interaction.setTaskee(getObject().unit.role.name());
        interaction.sendInteraction();
    }

    private void sendLLBMLMoveTask(){
        MoveInteraction interaction = new MoveInteraction();
        double deg = calculateMovementAngle();
        float rad = (float) Math.toRadians(deg + 180);
        interaction.setDirection(rad);
        interaction.setTaskee(getObject().unit.role.name());
        interaction.sendInteraction();
    }

    private double calculateMovementAngle() {
        Unit otherUnit = getObject().unit;
        Unit unit = getObject().otherUnit;

        Vector3D vectorBetweenUnits = otherUnit.rawData.posVector.subtract(unit.rawData.posVector);
        Vector3D vectorNorth = new Vector3D(0, 1, 0);

        double angle = Calculations.calculate360AngleBetween(vectorBetweenUnits, vectorNorth);
        System.out.println(angle);
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
