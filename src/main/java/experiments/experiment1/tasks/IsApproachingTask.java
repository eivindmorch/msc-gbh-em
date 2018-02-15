package experiments.experiment1.tasks;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import core.model.btree.Blackboard;
import experiments.experiment1.unit.FollowerUnit;

public class IsApproachingTask extends LeafTask<Blackboard<FollowerUnit>> {

    private double degreeLimit;

    public IsApproachingTask(double degreeLimit) {
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
        IsApproachingTask isApproachingTask = (IsApproachingTask) task;
        isApproachingTask.degreeLimit = this.degreeLimit;
        return isApproachingTask;
    }
}
