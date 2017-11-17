package training.btree.task;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class Wait extends LeafTask implements Named {

    private String name = "Wait";

    @Override
    public Status execute() {
        return Status.SUCCEEDED;
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
