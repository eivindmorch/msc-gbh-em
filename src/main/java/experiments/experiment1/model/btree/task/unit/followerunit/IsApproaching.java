package experiments.experiment1.model.btree.task.unit.followerunit;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import core.model.btree.task.NamedTask;
import core.model.btree.task.unit.VariableTask;
import experiments.experiment1.unit.FollowerUnit;

import static core.util.SystemUtil.random;

public class IsApproaching extends LeafTask<Blackboard<FollowerUnit>> implements NamedTask, VariableTask {

    private String name;
    private double degreeLimit;

    public IsApproaching() {
        this(1 + (random.nextDouble() * 44));
    }

    public IsApproaching(double degreeLimit) {
        this.degreeLimit = degreeLimit;
        this.name = "Is approaching [" + String.format("%.2f", degreeLimit) + "°]";
    }

    public IsApproaching(IsApproaching isApproachingTask) {
        this(isApproachingTask.degreeLimit);
    }

    @Override
    public Status execute() {
        Double otherUnitMovementAngleRelativeToMyPosition =
                getObject().getUnit().getFollowerProcessedDataRow().getTargetMovementAngleRelativeToFollowerPosition();

        if (otherUnitMovementAngleRelativeToMyPosition == null) {
            return Status.FAILED;
        }
        if (otherUnitMovementAngleRelativeToMyPosition < degreeLimit || otherUnitMovementAngleRelativeToMyPosition > 360 - degreeLimit) {
            return Status.SUCCEEDED;
        }
        return Status.FAILED;
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
        return new IsApproaching(this);
    }
}
