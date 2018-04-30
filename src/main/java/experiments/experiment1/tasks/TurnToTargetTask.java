package experiments.experiment1.tasks;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import core.model.btree.task.TaskTickTracker;
import core.simulation.federate.Federate;
import core.util.Geometer;
import core.util.LlbmlUtil;
import core.util.exceptions.IllegalArgumentCombinationException;
import experiments.experiment1.unit.Experiment1Unit;
import experiments.experiment1.unit.FollowerUnit;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.TurnToHeadingInteraction;


public class TurnToTargetTask extends LeafTask<Blackboard<FollowerUnit>> {

    private final TaskTickTracker taskTickTracker = new TaskTickTracker(1);

    @Override
    public Status execute() {
        if (taskTickTracker.getCurrentStatus() == TaskTickTracker.Status.FIRST) {
            sendLLBMLTurnToHeadingTask();
        }
        taskTickTracker.tick();
        if (taskTickTracker.getCurrentStatus() == TaskTickTracker.Status.DONE) {
            return Status.SUCCEEDED;
        }
        return Status.RUNNING;
    }

    @Override
    public void start() {
        super.start();
        taskTickTracker.reset();
    }

    private void sendLLBMLTurnToHeadingTask() {
        TurnToHeadingInteraction interaction = new TurnToHeadingInteraction();

        double deg;
        try {
            deg = calculateHeadingAngle();
        } catch (IllegalArgumentCombinationException e) {
            return;
        }
        deg = LlbmlUtil.normaliseDegForLlbml(deg);

        float rad = (float) Math.toRadians(deg);
        interaction.setHeading(rad);
        interaction.setTaskee(getObject().getUnit().getMarking());

        Federate.getInstance().sendInteraction(interaction);
    }

    private double calculateHeadingAngle() throws IllegalArgumentCombinationException {
        FollowerUnit unit = getObject().getUnit();
        Experiment1Unit target = getObject().getUnit().getTarget();

        return(Geometer.absoluteBearing(unit.getRawDataRow().getLla(), target.getRawDataRow().getLla()));
    }

    protected Task<Blackboard<FollowerUnit>> copyTo(Task<Blackboard<FollowerUnit>> task) {
        return task;
    }

}
