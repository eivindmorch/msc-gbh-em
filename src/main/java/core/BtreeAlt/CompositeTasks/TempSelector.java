package core.BtreeAlt.CompositeTasks;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import core.BtreeAlt.TempTask;

import java.util.Arrays;
import java.util.List;

public class TempSelector extends TempCompositeTask {

    // For .newInstance()
    public TempSelector() {
    }

    public TempSelector(TempTask... children) {
        this(Arrays.asList(children));
    }

    public TempSelector(List<TempTask> children) {
        addChildren(children);
    }

    @Override
    public String getDisplayName() {
        return "?";
    }

    @Override
    public TempTask cloneTask() {
        TempSelector tempSelector = new TempSelector();
        for (TempTask child : getChildren()) {
            tempSelector.addChild(child.cloneTask());
        }
        return tempSelector;
    }

    @Override
    public Task instantiateTask() {
        return new Selector(instantiateChildren());
    }
}
