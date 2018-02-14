package experiments.experiment1.model.btree.task.unit.followerunit;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import core.model.btree.task.AlwaysSuccessfulTask;
import core.model.btree.task.NamedTask;
import core.model.btree.task.TaskTickTracker;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.TurnToHeadingInteraction;
import experiments.experiment1.unit.Experiment1Unit;
import experiments.experiment1.unit.FollowerUnit;
import core.util.Geometer;
import core.util.LlbmlUtil;
import core.util.exceptions.IllegalArgumentCombinationException;


public class TurnToTargetTask extends LeafTask<Blackboard<FollowerUnit>> implements NamedTask, AlwaysSuccessfulTask {

    private final String name = "Turn to target";
    private final TaskTickTracker taskTickTracker = new TaskTickTracker(1);

    @Override
    public Task.Status execute() {
        if (taskTickTracker.getCurrentStatus() == TaskTickTracker.Status.FIRST) {
            sendLLBMLTurnToHeadingTask();
        }
        taskTickTracker.tick();
        if (taskTickTracker.getCurrentStatus() == TaskTickTracker.Status.DONE) {
            return Status.SUCCEEDED;
        }
        return Task.Status.RUNNING;
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
        interaction.sendInteraction();
    }

    private double calculateHeadingAngle() throws IllegalArgumentCombinationException {
        FollowerUnit unit = getObject().getUnit();
        Experiment1Unit target = getObject().getUnit().getTarget();

        return(Geometer.absoluteBearing(unit.getRawDataRow().getLla(), target.getRawDataRow().getLla()));
    }

    protected Task<Blackboard<FollowerUnit>> copyTo(Task<Blackboard<FollowerUnit>> task) {
        return task;
    }

    @Override
    public String getName() {
        return name;
    }
}
