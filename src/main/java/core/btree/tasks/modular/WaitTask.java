package core.btree.tasks.modular;

import core.btree.tasks.executable.WaitTaskExec;
import core.btree.tasks.modular.template.leaf.LeafTask;
import core.btree.tasks.modular.template.AlwaysSuccessfulTask;
import core.btree.tasks.modular.template.Task;

public class WaitTask extends LeafTask implements AlwaysSuccessfulTask {

    @Override
    public String getDisplayName() {
        return "Wait";
    }

    @Override
    public Task cloneTask() {
        return new WaitTask();
    }

    @Override
    public com.badlogic.gdx.ai.btree.Task instantiateTask() {
        return new WaitTaskExec();
    }
}
