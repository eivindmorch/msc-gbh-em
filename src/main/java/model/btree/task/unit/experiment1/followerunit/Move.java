package model.btree.task.unit.experiment1.followerunit;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import model.Lla;
import model.btree.Blackboard;
import model.btree.task.NamedTask;
import no.ffi.hlalib.datatypes.fixedRecordData.GeodeticLocationStruct;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.MoveToLocationInteraction;
import unit.experiment1.Experiment1Unit;
import unit.experiment1.FollowerUnit;
import util.Geometer;
import util.exceptions.IllegalArgumentCombinationException;


public class Move extends LeafTask<Blackboard<FollowerUnit>> implements NamedTask {

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

        Lla currentLla = getObject().getUnit().getRawDataRow().getLla();
        Lla destinationLla = Geometer.getDestinationPointFromAzimuthAngle(currentLla, deg, 100);

        GeodeticLocationStruct geoLocationStruct = new GeodeticLocationStruct(
                (float)destinationLla.getLatitude(),
                (float)destinationLla.getLongitude(),
                (float)destinationLla.getAltitude()
        );

        interaction.setDestination(geoLocationStruct);
        interaction.setTaskee(getObject().getUnit().getMarking());
        interaction.sendInteraction();
    }

    private double calculateMovementAngle() throws IllegalArgumentCombinationException {
        FollowerUnit unit = getObject().getUnit();
        Experiment1Unit target = getObject().getUnit().getTarget();

        return(Geometer.absoluteBearing(unit.getRawDataRow().getLla(), target.getRawDataRow().getLla()));
    }

    @Override
    protected Task<Blackboard<FollowerUnit>> copyTo(Task<Blackboard<FollowerUnit>> task) {
        return task;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
