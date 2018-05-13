package experiments.experiment1.tasks;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.btree.Blackboard;
import experiments.experiment1.unit.FollowerUnit;


public class IsWithinTask extends LeafTask<Blackboard<FollowerUnit>> {

    private double distanceLimit;

    public IsWithinTask(double distanceLimit) {
        this.distanceLimit = distanceLimit;
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
        IsWithinTask isWithinTask = (IsWithinTask) task;
        isWithinTask.distanceLimit = this.distanceLimit;
        return isWithinTask;
    }

    @Override
    public String toString() {
        return "Is within " + distanceLimit;
    }
}
