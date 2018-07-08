package experiments.experiment1.tasks.modular;

import core.btree.tasks.blueprint.template.leaf.LeafTask;
import core.btree.tasks.blueprint.template.AlwaysSuccessfulTask;
import core.btree.tasks.blueprint.template.Task;
import experiments.experiment1.tasks.executable.TurnToTargetTaskExec;

public class TurnToTargetTask extends LeafTask implements AlwaysSuccessfulTask {

    @Override
    public String getDisplayName() {
        return "Turn to target";
    }

    @Override
    public Task cloneTask() {
        return new TurnToTargetTask();
    }

    @Override
    public com.badlogic.gdx.ai.btree.Task instantiateExecutableTask() {
        return new TurnToTargetTaskExec();
    }
}
