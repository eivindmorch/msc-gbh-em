package experiments.experiment1.model.btree.task.unit.followerunit;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.Lla;
import core.model.btree.Blackboard;
import core.model.btree.task.NamedTask;
import core.model.btree.task.TaskTickTracker;
import core.model.btree.task.VariableTask;
import no.ffi.hlalib.datatypes.fixedRecordData.GeodeticLocationStruct;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.MoveToLocationInteraction;
import experiments.experiment1.unit.Experiment1Unit;
import experiments.experiment1.unit.FollowerUnit;
import core.util.Geometer;
import core.util.exceptions.IllegalArgumentCombinationException;

import static core.util.SystemUtil.random;

public class MoveInTargetDirection extends LeafTask<Blackboard<FollowerUnit>> implements NamedTask, VariableTask {

    private int ticksToRun;
    private TaskTickTracker tickTracker;
    private String name;

    public MoveInTargetDirection() {
        randomiseTicksToRun();
    }

    public MoveInTargetDirection(int ticksToRun) {
        setTicksToRun(ticksToRun);
    }

    public MoveInTargetDirection(MoveInTargetDirection moveInTargetDirection) {
        this(moveInTargetDirection.ticksToRun);
    }

    @Override
    public Status execute() {
        if (tickTracker.getCurrentTick() == 0) {
            sendLLBMLMoveToLocationTask();
        }
        return tickTracker.tick();
    }

    private void sendLLBMLMoveToLocationTask(){
        MoveToLocationInteraction interaction = new MoveToLocationInteraction();
        double deg;

        try {
            deg = calculateMovementAngle();
        } catch (IllegalArgumentCombinationException e) {
            return;
        }

        Lla currentLla = getObject().getUnit().getRawDataRow().getLla();
        Lla destinationLla = Geometer.getDestinationPointFromAzimuthAngle(currentLla, deg, 30);

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

    @Override
    public void randomiseVariables() {
        randomiseTicksToRun();
    }

    private void randomiseTicksToRun() {
        setTicksToRun(1 + random.nextInt(9));
    }

    private void setTicksToRun(int ticksToRun) {
        this.ticksToRun = ticksToRun;
        this.tickTracker = new TaskTickTracker(ticksToRun);
        this.name = "Move in target direction (" + ticksToRun + ")";
    }
}
