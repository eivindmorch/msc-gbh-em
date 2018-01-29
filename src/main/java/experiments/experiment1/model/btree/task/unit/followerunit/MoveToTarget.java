package experiments.experiment1.model.btree.task.unit.followerunit;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.Lla;
import core.model.btree.Blackboard;
import core.model.btree.task.NamedTask;
import core.model.btree.task.TaskTickTracker;
import core.model.btree.task.unit.VariableTask;
import no.ffi.hlalib.datatypes.fixedRecordData.GeodeticLocationStruct;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.FollowUnitInteraction;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.MoveToLocationInteraction;
import experiments.experiment1.unit.FollowerUnit;

import static core.util.SystemUtil.random;


public class MoveToTarget extends LeafTask<Blackboard<FollowerUnit>> implements NamedTask, VariableTask {

    private int ticksToRun;
    private String name;

    private TaskTickTracker tickTracker = new TaskTickTracker(ticksToRun);

    public MoveToTarget() {
        this(1 + random.nextInt(9));
    }

    public MoveToTarget(int ticksToRun) {
        this.ticksToRun = ticksToRun;
        this.name = "Move to target (" + ticksToRun + ")";
    }

    public MoveToTarget(MoveToTarget moveToTargetTask) {
        this(moveToTargetTask.ticksToRun);
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

        Lla destinationLla = getObject().getUnit().getTarget().getRawDataRow().getLla();

        GeodeticLocationStruct geoLocationStruct = new GeodeticLocationStruct(
                (float)destinationLla.getLatitude(),
                (float)destinationLla.getLongitude(),
                (float)destinationLla.getAltitude()
        );

        interaction.setDestination(geoLocationStruct);
        interaction.setTaskee(getObject().getUnit().getMarking());
        interaction.sendInteraction();
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
    public Task<Blackboard<FollowerUnit>> cloneTask() {
        return new MoveToTarget(ticksToRun);
    }

    // TODO Variables = speed, ticks

    // TODO Variable limits
    // TODO increaseVariable() // with checks for min and max value
    // TODO decreaseVariable() // -||-
    // TODO randomiseVariable()

}
