package core.btree.tasks.blueprint;

import core.btree.tasks.executable.WaitTaskExec;
import core.btree.tasks.blueprint.template.leaf.LeafTask;
import core.btree.tasks.blueprint.template.AlwaysSuccessfulTask;
import core.btree.tasks.blueprint.template.Task;

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
    public com.badlogic.gdx.ai.btree.Task instantiateExecutableTask() {
        return new WaitTaskExec();
    }
}
