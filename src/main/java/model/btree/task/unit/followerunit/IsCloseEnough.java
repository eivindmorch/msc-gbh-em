package model.btree.task.unit.followerunit;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import model.btree.Blackboard;
import model.btree.task.Named;
import unit.FollowerUnit;

public class IsCloseEnough extends LeafTask<Blackboard<FollowerUnit>> implements Named {

    private final String name = "Is close enough?";
    private double distanceLimit;

    public IsCloseEnough() {
        this.distanceLimit = 10;
    }

    public IsCloseEnough(double distanceLimit) {
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
        return task;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
