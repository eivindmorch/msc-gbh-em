package experiments.experiment1.model.btree.task.unit.followerunit;

import com.badlogic.gdx.ai.btree.Task;
import core.model.Lla;
import core.model.btree.Blackboard;
import core.model.btree.task.NamedTask;
import core.model.btree.task.TaskTickTracker;
import core.model.btree.task.VariableLeafTask;
import no.ffi.hlalib.datatypes.arrayData.ArrayOfTaskIds;
import no.ffi.hlalib.datatypes.fixedRecordData.GeodeticLocationStruct;
import no.ffi.hlalib.datatypes.fixedRecordData.TaskId;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.MoveToLocationInteraction;
import experiments.experiment1.unit.FollowerUnit;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTaskManagement.CancelAllTasksInteraction;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTaskManagement.CancelSpecifiedTasksInteraction;

import static core.util.SystemUtil.random;

public class MoveToTargetTask extends VariableLeafTask<Blackboard<FollowerUnit>> implements NamedTask {

    private int ticksToRun;
    private TaskTickTracker tickTracker;
    private String name;

    private TaskId taskId;

    public MoveToTargetTask() {
        randomiseTicksToRun();
    }

    public MoveToTargetTask(int ticksToRun) {
        setTicksToRun(ticksToRun);
    }

    public MoveToTargetTask(MoveToTargetTask moveToTargetTask) {
        this(moveToTargetTask.ticksToRun);
    }

    @Override
    public Status execute() {
        if (tickTracker.getCurrentTick() == 0) {
            sendLLBMLMoveToLocationTask();
        } else if (tickTracker.getCurrentTick() == ticksToRun) {
            sendCancelTaskInteraction();
        }
        return tickTracker.tick();
    }

    @Override
    public void start() {
        super.start();
        this.tickTracker = new TaskTickTracker(ticksToRun);
    }

    private void sendLLBMLMoveToLocationTask(){
        taskId = new TaskId();

        MoveToLocationInteraction interaction = new MoveToLocationInteraction();
        interaction.setTask(taskId);

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

    private void sendCancelTaskInteraction() {
        // TODO CancelSpecifiedTask slows down simengine drastically
//        CancelSpecifiedTasksInteraction cancelSpecifiedTasksInteraction = new CancelSpecifiedTasksInteraction();
//        cancelSpecifiedTasksInteraction.setTasks(new ArrayOfTaskIds(taskId));
//        cancelSpecifiedTasksInteraction.sendInteraction();
        new CancelAllTasksInteraction().sendInteraction();
    }

    @Override
    protected Task<Blackboard<FollowerUnit>> copyTo(Task<Blackboard<FollowerUnit>> task) {
        return new MoveToTargetTask(this);
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
        this.name = "Move to target (" + ticksToRun + ")";
    }
}