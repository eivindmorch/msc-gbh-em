package experiments.experiment1.tasks;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.data.Lla;
import core.btree.Blackboard;
import core.btree.task.TaskTickTracker;
import core.simulation.hla.HlaManager;
import experiments.experiment1.unit.FollowerUnit;
import no.ffi.hlalib.datatypes.fixedRecordData.GeodeticLocationStruct;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.MoveToLocationInteraction;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.WaitInteraction;


public class MoveToTargetTask extends LeafTask<Blackboard<FollowerUnit>> {

    private final TaskTickTracker taskTickTracker = new TaskTickTracker(1);

    // TODO Fix usage of wait interaction
    @Override
    public Status execute() {
        if (taskTickTracker.getCurrentStatus() == TaskTickTracker.Status.FIRST) {
            sendLLBMLMoveToLocationTask();
        }
        taskTickTracker.tick();
        if (taskTickTracker.getCurrentStatus() == TaskTickTracker.Status.DONE) {
            sendLLBMLWaitTask();
            return Status.SUCCEEDED;
        }
        return Status.RUNNING;
    }

    @Override
    public void start() {
        super.start();
        taskTickTracker.reset();
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

        HlaManager.getInstance().sendInteraction(interaction);
    }

    private void sendLLBMLWaitTask(){
        WaitInteraction interaction = new WaitInteraction();
        interaction.setTaskee(getObject().getUnit().getMarking());

        HlaManager.getInstance().sendInteraction(interaction);
    }

    @Override
    protected Task<Blackboard<FollowerUnit>> copyTo(Task<Blackboard<FollowerUnit>> task) {
        return task;
    }
}
