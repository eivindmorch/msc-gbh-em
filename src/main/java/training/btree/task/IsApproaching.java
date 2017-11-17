package training.btree.task;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class IsApproaching extends LeafTask implements Named {

    private String name = "Is approaching?";

    @Override
    public Status execute() {
        return Status.FAILED;
    }

    @Override
    protected Task copyTo(Task task) {
        return task;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
