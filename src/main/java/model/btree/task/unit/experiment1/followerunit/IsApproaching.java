package model.btree.task.unit.experiment1.followerunit;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import model.btree.Blackboard;
import model.btree.task.NamedTask;
import unit.experiment1.FollowerUnit;

public class IsApproaching extends LeafTask<Blackboard<FollowerUnit>> implements NamedTask {

    private final String name = "Is approaching?";
    private double degreeLimit;

    public IsApproaching() {
        this.degreeLimit = 45;
    }

    public IsApproaching(double degreeLimit) {
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
        return task;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
