package training.btree.task;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class IsCloseEnough extends LeafTask {
    @Override
    public Status execute() {
        return Status.FAILED;
    }

    @Override
    protected Task copyTo(Task task) {
        return null;
    }
}
