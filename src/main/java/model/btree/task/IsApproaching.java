package model.btree.task;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import model.btree.Blackboard;

public class IsApproaching extends LeafTask<Blackboard> implements Named {

    private final String name = "Is approaching?";

    @Override
    public Status execute() {
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
