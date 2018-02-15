package core.model.btree.task.unit.temp;

import com.badlogic.gdx.ai.btree.Task;
import core.BtreeAlt.LeafTasks.TempLeafTask;
import core.BtreeAlt.TempAlwaysSuccessfulTask;
import core.BtreeAlt.TempTask;
import core.model.btree.task.unit.WaitTask;

public class TempWaitTask extends TempLeafTask implements TempAlwaysSuccessfulTask {


    public TempWaitTask() {
        super("Wait");
    }

    @Override
    public TempTask cloneTask() {
        return new TempWaitTask();
    }

    @Override
    public Task instantiateTask() {
        return new WaitTask();
    }
}
