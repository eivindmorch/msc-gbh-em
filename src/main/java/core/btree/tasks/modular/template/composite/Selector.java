package core.btree.tasks.modular.template.composite;

import core.btree.tasks.modular.template.Task;

import java.util.Arrays;
import java.util.List;

public class Selector extends CompositeTask {

    // For .newInstance()
    public Selector() {
    }

    public Selector(Task... children) {
        this(Arrays.asList(children));
    }

    public Selector(List<Task> children) {
        addChildren(children);
    }

    @Override
    public String getDisplayName() {
        return " ? ";
    }

    @Override
    public Task cloneTask() {
        Selector selector = new Selector();
        for (Task child : getChildren()) {
            selector.addChild(child.cloneTask());
        }
        return selector;
    }

    @Override
    public com.badlogic.gdx.ai.btree.Task instantiateExecutableTask() {
        return new com.badlogic.gdx.ai.btree.branch.Selector(instantiateChildren());
    }
}
