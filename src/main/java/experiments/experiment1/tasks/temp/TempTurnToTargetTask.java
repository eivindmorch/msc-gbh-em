package experiments.experiment1.tasks.temp;

import com.badlogic.gdx.ai.btree.Task;
import core.BtreeAlt.LeafTasks.TempLeafTask;
import core.BtreeAlt.TempAlwaysSuccessfulTask;
import core.BtreeAlt.TempTask;
import experiments.experiment1.tasks.TurnToTargetTask;

public class TempTurnToTargetTask extends TempLeafTask implements TempAlwaysSuccessfulTask {

    @Override
    public String getDisplayName() {
        return "Turn to target";
    }

    @Override
    public TempTask cloneTask() {
        return new TempTurnToTargetTask();
    }

    @Override
    public Task instantiateTask() {
        return new TurnToTargetTask();
    }
}
