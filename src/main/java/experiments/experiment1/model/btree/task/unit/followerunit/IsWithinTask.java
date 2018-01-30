package experiments.experiment1.model.btree.task.unit.followerunit;

import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import core.model.btree.task.NamedTask;
import core.model.btree.task.VariableLeafTask;
import experiments.experiment1.unit.FollowerUnit;

import static core.util.SystemUtil.random;

// TODO Rename
public class IsWithinTask extends VariableLeafTask<Blackboard<FollowerUnit>> implements NamedTask {

    private double distanceLimit;
    private String name;

    public IsWithinTask() {
        randomiseDistanceLimit();
    }

    public IsWithinTask(double distanceLimit) {
        setDistanceLimit(distanceLimit);
    }

    public IsWithinTask(IsWithinTask isWithinTaskTask) {
        this(isWithinTaskTask.distanceLimit);
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
        return new IsWithinTask(this);
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
