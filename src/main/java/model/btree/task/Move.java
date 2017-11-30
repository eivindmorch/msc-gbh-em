package model.btree.task;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import model.Lla;
import no.ffi.hlalib.datatypes.fixedRecordData.GeodeticLocationStruct;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.MoveToLocationInteraction;
import unit.FollowerUnit;
import unit.Unit;
import util.exceptions.IllegalArgumentCombinationException;
import model.btree.Blackboard;
import util.Geometer;


public class Move extends LeafTask<Blackboard> implements Named {

    private final String name = "Move";

    @Override
    public Status execute() {
        sendLLBMLTurnToHeadingTask();
        return Status.SUCCEEDED;
    }

    private void sendLLBMLTurnToHeadingTask(){
        MoveToLocationInteraction interaction = new MoveToLocationInteraction();
        double deg;

        try {
            deg = calculateMovementAngle();
        } catch (IllegalArgumentCombinationException e) {
            return;
        }

        Lla currentLla = getObject().getFollowerUnit().getRawData().getLla();
        Lla destinationLla = Geometer.getDestinationPointFromAzimuthAngle(currentLla, deg, 100);

        GeodeticLocationStruct geoLocationStruct = new GeodeticLocationStruct(
                (float)destinationLla.getLatitude(),
                (float)destinationLla.getLongitude(),
                (float)destinationLla.getAltitude()
        );

        interaction.setDestination(geoLocationStruct);
        interaction.setTaskee(getObject().getFollowerUnit().getMarking());
        interaction.sendInteraction();
    }

    private double calculateMovementAngle() throws IllegalArgumentCombinationException {
        FollowerUnit unit = getObject().getFollowerUnit();
        Unit otherUnit = getObject().getFollowerUnit().getTarget();

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
