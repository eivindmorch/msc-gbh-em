package training.btree.task;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class Move extends LeafTask {


    @Override
    public Status execute() {
        return Status.SUCCEEDED;
    }

    @Override
    protected Task copyTo(Task task) {
        return task;
    }
}
