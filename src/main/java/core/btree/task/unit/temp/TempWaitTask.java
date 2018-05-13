package core.btree.task.unit.temp;

import com.badlogic.gdx.ai.btree.Task;
import core.BtreeAlt.LeafTasks.TempLeafTask;
import core.BtreeAlt.TempAlwaysSuccessfulTask;
import core.BtreeAlt.TempTask;
import core.btree.task.unit.WaitTask;

public class TempWaitTask extends TempLeafTask implements TempAlwaysSuccessfulTask {

    @Override
    public String getDisplayName() {
        return "Wait";
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
