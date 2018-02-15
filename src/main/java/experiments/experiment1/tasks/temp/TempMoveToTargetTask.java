package experiments.experiment1.tasks.temp;

import com.badlogic.gdx.ai.btree.Task;
import core.BtreeAlt.LeafTasks.TempLeafTask;
import core.BtreeAlt.TempAlwaysSuccessfulTask;
import core.BtreeAlt.TempTask;
import experiments.experiment1.tasks.MoveToTargetTask;

public class TempMoveToTargetTask extends TempLeafTask implements TempAlwaysSuccessfulTask {

    @Override
    public String getDisplayName() {
        return "Move to target";
    }

    @Override
    public TempTask cloneTask() {
        return new TempMoveToTargetTask();
    }

    @Override
    public Task instantiateTask() {
        return new MoveToTargetTask();
    }
}
