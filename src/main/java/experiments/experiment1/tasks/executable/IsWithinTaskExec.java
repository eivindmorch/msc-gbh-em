package experiments.experiment1.tasks.executable;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.btree.Blackboard;
import experiments.experiment1.units.FollowerUnit;


public class IsWithinTaskExec extends LeafTask<Blackboard<FollowerUnit>> {

    private double distanceLimit;

    public IsWithinTaskExec(double distanceLimit) {
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
        IsWithinTaskExec isWithinTaskExec = (IsWithinTaskExec) task;
        isWithinTaskExec.distanceLimit = this.distanceLimit;
        return isWithinTaskExec;
    }

    @Override
    public String toString() {
        return "Is within " + distanceLimit;
    }
}
