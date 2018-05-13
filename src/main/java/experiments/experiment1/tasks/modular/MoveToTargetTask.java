package experiments.experiment1.tasks.modular;

import core.btree.tasks.modular.template.Task;
import core.btree.tasks.modular.template.leaf.LeafTask;
import core.btree.tasks.modular.template.AlwaysSuccessfulTask;
import experiments.experiment1.tasks.executable.MoveToTargetTaskExec;

public class MoveToTargetTask extends LeafTask implements AlwaysSuccessfulTask {

    @Override
    public String getDisplayName() {
        return "Move to target";
    }

    @Override
    public Task cloneTask() {
        return new MoveToTargetTask();
    }

    @Override
    public com.badlogic.gdx.ai.btree.Task instantiateTask() {
        return new MoveToTargetTaskExec();
    }
}
