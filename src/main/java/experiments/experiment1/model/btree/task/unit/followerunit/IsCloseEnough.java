package experiments.experiment1.model.btree.task.unit.followerunit;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import core.model.btree.task.NamedTask;
import core.model.btree.task.unit.VariableTask;
import experiments.experiment1.unit.FollowerUnit;

import static core.util.SystemUtil.random;

// TODO Rename
public class IsCloseEnough extends LeafTask<Blackboard<FollowerUnit>> implements NamedTask, VariableTask {

    private double distanceLimit;
    private String name;

    public IsCloseEnough() {
        this(random.nextDouble() * 50);
    }

    public IsCloseEnough(double distanceLimit) {
        this.distanceLimit = distanceLimit;
        this.name = "Is within [" + String.format("%.1f", this.distanceLimit) + "m]";
    }

    public IsCloseEnough(IsCloseEnough isCloseEnoughTask) {
        this(isCloseEnoughTask.distanceLimit);
    }

    @Override
    public Status execute() {
        double distance = getObject().getUnit().getFollowerProcessedDataRow().getDistanceToTarget();
        if (distance < distanceLimit) {
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
        return new IsCloseEnough(this);
    }

    // TODO Variable limits
    // TODO increaseVariable() // with checks for min and max value
    // TODO decreaseVariable() // -||-
    // TODO randomiseVariable()
}
