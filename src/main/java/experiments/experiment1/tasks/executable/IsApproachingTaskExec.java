package experiments.experiment1.tasks.executable;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.btree.Blackboard;
import experiments.experiment1.unit.FollowerUnit;

public class IsApproachingTaskExec extends LeafTask<Blackboard<FollowerUnit>> {

    private double degreeLimit;

    public IsApproachingTaskExec(double degreeLimit) {
        this.degreeLimit = degreeLimit;
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
        IsApproachingTaskExec isApproachingTaskExec = (IsApproachingTaskExec) task;
        isApproachingTaskExec.degreeLimit = this.degreeLimit;
        return isApproachingTaskExec;
    }

    @Override
    public String toString() {
        return "Is Approaching " + degreeLimit;
    }
}
