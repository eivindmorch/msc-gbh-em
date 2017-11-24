package model.btree.task;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import model.btree.Blackboard;

public class IsCloseEnough extends LeafTask<Blackboard> implements Named {

    private final String name = "Is close enough?";

    @Override
    public Status execute() {
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
