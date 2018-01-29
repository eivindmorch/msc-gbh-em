package experiments.experiment1.model.btree.task.unit.followerunit;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import core.model.btree.task.NamedTask;
import core.model.btree.task.VariableTask;
import experiments.experiment1.unit.FollowerUnit;

import static core.util.SystemUtil.random;

// TODO Rename
public class IsWithin extends LeafTask<Blackboard<FollowerUnit>> implements NamedTask, VariableTask {

    private double distanceLimit;
    private String name;

    public IsWithin() {
        randomiseDistanceLimit();
    }

    public IsWithin(double distanceLimit) {
        setDistanceLimit(distanceLimit);
    }

    public IsWithin(IsWithin isWithinTask) {
        this(isWithinTask.distanceLimit);
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
    public void randomiseVariables() {
        randomiseDistanceLimit();
    }

    private void randomiseDistanceLimit() {
        setDistanceLimit(random.nextDouble() * 50);
    }

    private void setDistanceLimit(double distanceLimit) {
        this.distanceLimit = distanceLimit;
        this.name = "Is within [" + String.format("%.1f", this.distanceLimit) + "m]";
    }
}
