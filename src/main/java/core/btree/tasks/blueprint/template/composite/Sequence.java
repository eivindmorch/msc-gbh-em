package core.btree.tasks.blueprint.template.composite;

import core.btree.tasks.blueprint.template.Task;

import java.util.Arrays;
import java.util.List;

public class Sequence extends CompositeTask {

    // For .newInstance()
    public Sequence() {
    }

    public Sequence(Task... children) {
        this(Arrays.asList(children));
    }

    public Sequence(List<Task> children) {
        addChildren(children);
    }

    @Override
    public String getDisplayName() {
        return "→";
    }

    @Override
    public Task cloneTask() {
        Sequence sequence = new Sequence();
        for (Task child : getChildren()) {
            sequence.addChild(child.cloneTask());
        }
        return sequence;
    }

    @Override
    public com.badlogic.gdx.ai.btree.Task instantiateExecutableTask() {
        return new com.badlogic.gdx.ai.btree.branch.Sequence(instantiateChildren());
    }
}
