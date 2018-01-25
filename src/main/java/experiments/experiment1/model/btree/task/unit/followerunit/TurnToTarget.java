package experiments.experiment1.model.btree.task.unit.followerunit;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import core.model.btree.task.NamedTask;
import core.model.btree.task.TaskTickTracker;
import no.ffi.hlalib.interactions.HLAinteractionRoot.LBMLMessage.LBMLTask.TurnToHeadingInteraction;
import experiments.experiment1.unit.Experiment1Unit;
import experiments.experiment1.unit.FollowerUnit;
import core.util.Geometer;
import core.util.LlbmlUtil;
import core.util.exceptions.IllegalArgumentCombinationException;

public class TurnToTarget extends LeafTask<Blackboard<FollowerUnit>> implements NamedTask {

    private final String name = "Turn to heading";

    private TaskTickTracker tickTracker = new TaskTickTracker(20);

    @Override
    public Status execute() {
        if (tickTracker.getCurrentTick() == 0) {
            sendLLBMLTurnToHeadingTask(getObject().getUnit().getMarking());
        }
        return tickTracker.tick();
    }

    private void sendLLBMLTurnToHeadingTask(String entityMarkingString) {
        TurnToHeadingInteraction interaction = new TurnToHeadingInteraction();
        interaction.setTaskee(entityMarkingString);

        double deg;

        try {
            deg = calculateHeadingAngle();
        } catch (IllegalArgumentCombinationException e) {
            return;
        }

        deg = LlbmlUtil.normaliseDegForLlbml(deg);

        float rad = (float) Math.toRadians(deg);
        interaction.setHeading(rad);
        interaction.setTaskee(entityMarkingString);
        interaction.sendInteraction();
    }

    private double calculateHeadingAngle() throws IllegalArgumentCombinationException {
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
        return name;
    }
}
