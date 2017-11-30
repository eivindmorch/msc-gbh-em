package model.btree.task.follower;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import model.btree.Blackboard;
import model.btree.task.Named;

public class IsCloseEnough extends LeafTask<Blackboard> implements Named {

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
        double distance = getObject().getFollowerUnit().getProcessedData().getDistance();
        if (distance < distanceLimit) {
            return Status.SUCCEEDED;
        }
        return Status.FAILED;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
