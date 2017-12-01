package model.btree.task.follower;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import model.btree.Blackboard;
import model.btree.task.Named;

public class IsApproaching extends LeafTask<Blackboard> implements Named {

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
                getObject().getFollowerUnit().getFollowerProcessedDataRow().getTargetMovementAngleRelativeToFollowerPosition();

        if (otherUnitMovementAngleRelativeToMyPosition == null) {
            return Status.FAILED;
        }
        if (otherUnitMovementAngleRelativeToMyPosition < degreeLimit || otherUnitMovementAngleRelativeToMyPosition > 360 - degreeLimit) {
            return Status.SUCCEEDED;
        }
        return Status.FAILED;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        return task;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
